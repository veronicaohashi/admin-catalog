package com.fullcycle.admin.catalog.application.video.create;

import com.fullcycle.admin.catalog.application.Fixture;
import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.video.Video;
import com.fullcycle.admin.catalog.domain.video.VideoGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateVideoUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultCreateVideoUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Mock
    private GenreGateway genreGateway;

    @Mock
    private VideoGateway videoGateway;

    @Override
    protected List<Object> getMocks() {
        return null;
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
        final Resource expectedVideo = Fixture.Videos.resource(Type.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(Type.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(Type.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(Type.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(Type.THUMBNAIL_HALF);

        final var command = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchYear,
                expectedDuration,
                expectedRating,
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

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedMembers));

        when(videoGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        final var result = useCase.execute(command);

        Assertions.assertNotNull(result.id());
        verify(videoGateway).create(argThat(video ->
                Objects.equals(expectedTitle, video.getTitle())
                        && Objects.equals(expectedDescription, video.getDescription())
                        && Objects.equals(expectedLaunchYear, video.getLaunchedAt())
                        && Objects.equals(expectedDuration, video.getDuration())
                        && Objects.equals(expectedOpened, video.isOpened())
                        && Objects.equals(expectedPublished, video.isPublished())
                        && Objects.equals(expectedRating, video.getRating())
                        && Objects.equals(expectedCategories, video.getCategories())
                        && Objects.equals(expectedGenres, video.getGenres())
                        && Objects.equals(expectedMembers, video.getCastMembers())
                        && Objects.equals(expectedVideo.name(), video.getVideo().get().name())
                        && Objects.equals(expectedTrailer.name(), video.getTrailer().get().name())
                        && Objects.equals(expectedBanner.name(), video.getBanner().get().name())
                        && Objects.equals(expectedThumb.name(), video.getThumbnail().get().name())
                        && Objects.equals(expectedThumbHalf.name(), video.getThumbnailHalf().get().name())
        ));
    }
}
