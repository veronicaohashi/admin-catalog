package com.fullcycle.admin.catalog.infraestructure.services.local;

import com.fullcycle.admin.catalog.domain.Fixture;
import com.fullcycle.admin.catalog.domain.utils.IdUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class InMemoryStorageServiceTest {

    private InMemoryStorageService service = new InMemoryStorageService();

    @BeforeEach
    public void setUp() {
        service.clean();
    }

    @Test
    void givenValidResource_whenCallsStore_thenStoreIt() {
        final var expectedResource = Fixture.Videos.videoResource();
        final var expectedName = IdUtils.uuid();

        service.store(expectedName, expectedResource);

        Assertions.assertEquals(expectedResource, service.storage().get(expectedName));
    }

    @Test
    void givenValidResource_whenCallsGet_thenRetrieveIt() {
        final var expectedResource = Fixture.Videos.videoResource();
        final var expectedName = IdUtils.uuid();
        service.storage().put(expectedName, expectedResource);

        final var result = service.get(expectedName).get();

        Assertions.assertEquals(expectedResource, result);
    }

    @Test
    void givenInvalidResource_whenCallsGet_thenRetrieveEmpty() {
        final var result = service.get(IdUtils.uuid());

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void givenValidPrefix_whenCallsList_thenRetrieveIt() {
        final var expectedPrefix = "video";
        final var expectedNames = List.of(
                "video_" + IdUtils.uuid(),
                "video_" + IdUtils.uuid(),
                "video_" + IdUtils.uuid()
        );
        final var all = new ArrayList<>(expectedNames);
        all.add("image_" + IdUtils.uuid());
        all.add("image_" + IdUtils.uuid());
        all.forEach(name -> service.storage().put(name, Fixture.Videos.videoResource()));

        final var result = service.list(expectedPrefix);

        Assertions.assertTrue(expectedNames.containsAll(result));
    }

    @Test
    void givenValidNames_whenCallsDelete_thenDeleteAll() {
        final var videos = List.of(
                "video_" + IdUtils.uuid(),
                "video_" + IdUtils.uuid(),
                "video_" + IdUtils.uuid()
        );
        final var all = new ArrayList<>(videos);
        final var expectedNames = Set.of(
                "image_" + IdUtils.uuid(),
                "image_" + IdUtils.uuid()
        );
        all.addAll(expectedNames);
        all.forEach(name -> service.storage().put(name, Fixture.Videos.videoResource()));

        service.deleteAll(videos);

        Assertions.assertEquals(2, service.storage().size());
        Assertions.assertTrue(expectedNames.containsAll(service.storage().keySet()));
    }
}