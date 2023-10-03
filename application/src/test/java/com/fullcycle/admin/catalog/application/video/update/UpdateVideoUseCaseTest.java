package com.fullcycle.admin.catalog.application.video.update;

import com.fullcycle.admin.catalog.application.Fixture;
import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.video.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateVideoUseCaseTest extends UseCaseTest {

    @Mock
    private VideoGateway videoGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private GenreGateway genreGateway;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @InjectMocks
    private DefaultUpdateVideoUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway);
    }

    @Test
    void givenAValidCommand_whenCallsCreateVideo_thenReturnVideoId() {
        final var expectedTitle = Fixture.Videos.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchYear = Fixture.year();
        final var expectedDuration = Fixture.Videos.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.of(Fixture.Categories.scienceFiction().getId());
        final var expectedGenres = Set.of(Fixture.Genres.dystopian().getId());
        final var expectedMembers = Set.of(
                Fixture.CastMembers.jenniferLawrence().getId(),
                Fixture.CastMembers.kayaScodelario().getId()
        );
        final Resource expectedVideo = Fixture.Videos.resource(Resource.Type.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(Resource.Type.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(Resource.Type.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(Resource.Type.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(Resource.Type.THUMBNAIL_HALF);
        final var video = Fixture.Videos.systemDesign();
        final var command = UpdateVideoCommand.with(
                video.getId().getValue(),
                expectedTitle,
                expectedDescription,
                expectedLaunchYear.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedMembers),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumb,
                expectedThumbHalf
        );

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(video));

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedMembers));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        mockImageMedia();
        mockAudioVideoMedia();

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var result = useCase.execute(command);

        Assertions.assertNotNull(result.id());
        verify(videoGateway).update(argThat(updatedVideo ->
                Objects.equals(expectedTitle, updatedVideo.getTitle())
                        && Objects.equals(expectedDescription, updatedVideo.getDescription())
                        && Objects.equals(expectedLaunchYear, updatedVideo.getLaunchedAt())
                        && Objects.equals(expectedDuration, updatedVideo.getDuration())
                        && Objects.equals(expectedOpened, updatedVideo.isOpened())
                        && Objects.equals(expectedPublished, updatedVideo.isPublished())
                        && Objects.equals(expectedRating, updatedVideo.getRating())
                        && Objects.equals(expectedCategories, updatedVideo.getCategories())
                        && Objects.equals(expectedGenres, updatedVideo.getGenres())
                        && Objects.equals(expectedMembers, updatedVideo.getCastMembers())
                        && Objects.equals(expectedVideo.name(), updatedVideo.getVideo().get().name())
                        && Objects.equals(expectedTrailer.name(), updatedVideo.getTrailer().get().name())
                        && Objects.equals(expectedBanner.name(), updatedVideo.getBanner().get().name())
                        && Objects.equals(expectedThumb.name(), updatedVideo.getThumbnail().get().name())
                        && Objects.equals(expectedThumbHalf.name(), updatedVideo.getThumbnailHalf().get().name())
        ));
    }

    private void mockAudioVideoMedia() {
        when(mediaResourceGateway.storeAudioVideo(any(), any()))
                .thenAnswer(t -> {
                    final var resource = t.getArgument(1, Resource.class);
                    return AudioVideoMedia.with(UUID.randomUUID().toString(), resource.name(), "/img", "", MediaStatus.PENDING);
                });
    }

    private void mockImageMedia() {
        when(mediaResourceGateway.storeImage(any(), any()))
                .thenAnswer(t -> {
                    final var resource = t.getArgument(1, Resource.class);
                    return ImageMedia.with(UUID.randomUUID().toString(), resource.name(), "/img");
                });
    }
}
