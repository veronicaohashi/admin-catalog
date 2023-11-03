package com.fullcycle.admin.catalog.application.video.retrieve.get;

import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.Fixture;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.video.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;

class GetVideoByIdUseCaseTest extends UseCaseTest {

    @Mock
    private VideoGateway videoGateway;

    @InjectMocks
    private DefaultGetVideoByIdUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway);
    }

    @Test
    void givenAValidId_whenCallsGetVideo_thenReturnIt() {
        final var expectedTitle = Fixture.Videos.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchYear = Fixture.year();
        final var expectedDuration = Fixture.Videos.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.of(Fixture.Genres.dystopian().getId());
        final var expectedMembers = Set.of(
                Fixture.CastMembers.jenniferLawrence().getId(),
                Fixture.CastMembers.kayaScodelario().getId()
        );
        final AudioVideoMedia expectedVideo = Fixture.Videos.audioVideo();
        final AudioVideoMedia expectedTrailer = Fixture.Videos.audioTrailer();
        final ImageMedia expectedBanner = Fixture.Videos.imageBanner();
        final ImageMedia expectedThumb = Fixture.Videos.imageThumbnail();
        final ImageMedia expectedThumbHalf = Fixture.Videos.imageThumbnailHalf();
        final var video = Video.newVideo(
                        expectedTitle,
                        expectedDescription,
                        expectedLaunchYear,
                        expectedDuration,
                        expectedRating,
                        expectedOpened,
                        expectedPublished,
                        expectedCategories,
                        expectedGenres,
                        expectedMembers
                )
                .updateVideo(expectedVideo)
                .updateTrailer(expectedTrailer)
                .updateBanner(expectedBanner)
                .updateThumbnail(expectedThumb)
                .updateThumbnailHalf(expectedThumbHalf);
        final var expectedId = video.getId();
        when(videoGateway.findById(expectedId))
                .thenReturn(Optional.of(video));

        final var result = useCase.execute(expectedId.getValue());

        Assertions.assertEquals(expectedId.getValue(), result.id());
        Assertions.assertEquals(expectedTitle, result.title());
        Assertions.assertEquals(expectedDescription, result.description());
        Assertions.assertEquals(expectedLaunchYear.getValue(), result.launchedAt());
        Assertions.assertEquals(expectedDuration, result.duration());
        Assertions.assertEquals(expectedOpened, result.opened());
        Assertions.assertEquals(expectedPublished, result.published());
        Assertions.assertEquals(expectedRating, result.rating());
        Assertions.assertEquals(asString(expectedCategories), result.categories());
        Assertions.assertEquals(asString(expectedGenres), result.genres());
        Assertions.assertEquals(asString(expectedMembers), result.castMembers());
        Assertions.assertEquals(expectedVideo, result.video());
        Assertions.assertEquals(expectedTrailer, result.trailer());
        Assertions.assertEquals(expectedBanner, result.banner());
        Assertions.assertEquals(expectedThumb, result.thumbnail());
        Assertions.assertEquals(expectedThumbHalf, result.thumbnailHalf());
        Assertions.assertEquals(video.getCreatedAt(), result.createdAt());
        Assertions.assertEquals(video.getUpdatedAt(), result.updatedAt());
    }

    @Test
    void givenInvalidId_whenCallsGetVideo_thenReturnNotFound() {
        final var expectedErrorMessage = "Video with ID 123 was not found";
        final var invalidID = VideoID.from("123");
        when(videoGateway.findById(invalidID))
                .thenReturn(Optional.empty());

        final var error = Assertions.assertThrows(
                NotFoundException.class,
                () -> useCase.execute(invalidID.getValue())
        );

        Assertions.assertEquals(expectedErrorMessage, error.getMessage());
    }
}
