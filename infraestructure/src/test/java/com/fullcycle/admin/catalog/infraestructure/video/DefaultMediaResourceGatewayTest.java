package com.fullcycle.admin.catalog.infraestructure.video;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.Fixture;
import com.fullcycle.admin.catalog.domain.video.MediaStatus;
import com.fullcycle.admin.catalog.domain.video.VideoID;
import com.fullcycle.admin.catalog.domain.video.VideoMediaType;
import com.fullcycle.admin.catalog.domain.video.VideoResource;
import com.fullcycle.admin.catalog.infraestructure.services.StorageService;
import com.fullcycle.admin.catalog.infraestructure.services.local.InMemoryStorageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@IntegrationTest
class DefaultMediaResourceGatewayTest {
    @Autowired
    private DefaultMediaResourceGateway defaultMediaResourceGateway;

    @Autowired
    private StorageService storageService;

    @BeforeEach
    public void setUp() {
        storageService().clean();
    }

    @Test
    void givenValidResource_whenCallsStorageAudioVideo_shouldStoreIt() {
        final var expectedVideoId = VideoID.unique();
        final var expectedResource = Fixture.Videos.videoResource();
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedStatus = MediaStatus.PENDING;
        final var expectedLocation = "videoId-%s/type-%s".formatted(expectedVideoId.getValue(), expectedType.name());
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);

        final var result = defaultMediaResourceGateway.storeAudioVideo(expectedVideoId, expectedVideoResource);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.id());
        Assertions.assertEquals(expectedResource.checksum(), result.checksum());
        Assertions.assertEquals(expectedResource.name(), result.name());
        Assertions.assertEquals(expectedLocation, result.rawLocation());
        Assertions.assertEquals("", result.encodedPath());
        Assertions.assertEquals(expectedStatus, result.status());

        final var storedResource = storageService().storage().get(expectedLocation);

        Assertions.assertEquals(expectedResource, storedResource);
    }

    @Test
    void givenValidResource_whenCallsStorageImage_shouldStoreIt() {
        final var expectedVideoId = VideoID.unique();
        final var expectedResource = Fixture.Videos.bannerResource();
        final var expectedType = VideoMediaType.BANNER;
        final var expectedLocation = "videoId-%s/type-%s".formatted(expectedVideoId.getValue(), expectedType.name());
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);

        final var result = defaultMediaResourceGateway.storeImage(expectedVideoId, expectedVideoResource);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.id());
        Assertions.assertEquals(expectedResource.checksum(), result.checksum());
        Assertions.assertEquals(expectedResource.name(), result.name());
        Assertions.assertEquals(expectedLocation, result.location());

        final var storedResource = storageService().storage().get(expectedLocation);

        Assertions.assertEquals(expectedResource, storedResource);
    }

    @Test
    void givenValidVideoId_whenCallsClearResources_thenDeleteAll() {
        final var firstVideoId = VideoID.unique();
        final var secondVideoId = VideoID.unique();

        final var toBeDeleted = List.of(
                "videoId-%s/type-%s".formatted(firstVideoId.getValue(), VideoMediaType.VIDEO.name()),
                "videoId-%s/type-%s".formatted(firstVideoId.getValue(), VideoMediaType.TRAILER.name()),
                "videoId-%s/type-%s".formatted(firstVideoId.getValue(), VideoMediaType.BANNER.name())
        );

        final var toNotBeDeleted = List.of(
                "videoId-%s/type-%s".formatted(secondVideoId.getValue(), VideoMediaType.BANNER.name()),
                "videoId-%s/type-%s".formatted(secondVideoId.getValue(), VideoMediaType.THUMBNAIL.name())
        );

        toBeDeleted.forEach(it -> storageService().store(it, Fixture.Videos.videoResource()));
        toNotBeDeleted.forEach(it -> storageService().store(it, Fixture.Videos.bannerResource()));

        Assertions.assertEquals(5, storageService().storage().size());

        defaultMediaResourceGateway.clearResource(firstVideoId);

        Assertions.assertEquals(2, storageService().storage().size());
        Assertions.assertTrue(storageService().storage().keySet().containsAll(toNotBeDeleted));
    }

    private InMemoryStorageService storageService() {
        return (InMemoryStorageService) storageService;
    }
}