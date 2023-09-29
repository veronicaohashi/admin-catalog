package com.fullcycle.admin.catalog.application.video.create;

import com.fullcycle.admin.catalog.application.Fixture;
import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.InternalErrorException;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.video.*;
import com.fullcycle.admin.catalog.domain.video.Resource.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

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
    private MediaResourceGateway mediaResourceGateway;

    @Mock
    private VideoGateway videoGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(genreGateway, categoryGateway, castMemberGateway, mediaResourceGateway, videoGateway);
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

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedMembers));

        when(videoGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        mockImageMedia();
        mockAudioVideoMedia();

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

    @Test
    void givenAValidCommandWithoutCategories_whenCallsCreateVideo_thenReturnVideoId() {
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
        final Resource expectedVideo = Fixture.Videos.resource(Type.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(Type.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(Type.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(Type.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(Type.THUMBNAIL_HALF);

        final var command = CreateVideoCommand.with(
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

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedMembers));

        when(videoGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        mockImageMedia();
        mockAudioVideoMedia();

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

    @Test
    void givenAValidCommandWithoutGenres_whenCallsCreateVideo_thenReturnVideoId() {
        final var expectedTitle = Fixture.Videos.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchYear = Fixture.year();
        final var expectedDuration = Fixture.Videos.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.of(Fixture.Categories.scienceFiction().getId());
        final var expectedGenres = Set.<GenreID>of();
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

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedMembers));

        when(videoGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        mockImageMedia();
        mockAudioVideoMedia();

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

    @Test
    void givenAValidCommandWithoutCastMembers_whenCallsCreateVideo_thenReturnVideoId() {
        final var expectedTitle = Fixture.Videos.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchYear = Fixture.year();
        final var expectedDuration = Fixture.Videos.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.of(Fixture.Categories.scienceFiction().getId());
        final var expectedGenres = Set.of(Fixture.Genres.dystopian().getId());
        final var expectedMembers = Set.<CastMemberID>of();
        final Resource expectedVideo = Fixture.Videos.resource(Type.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(Type.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(Type.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(Type.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(Type.THUMBNAIL_HALF);

        final var command = CreateVideoCommand.with(
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

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        when(videoGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        mockImageMedia();
        mockAudioVideoMedia();

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

    @Test
    void givenAValidCommandWithoutResources_whenCallsCreateVideo_thenReturnVideoId() {
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

        final var command = CreateVideoCommand.with(
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
                null,
                null,
                null,
                null,
                null
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
                        && video.getVideo().isEmpty()
                        && video.getTrailer().isEmpty()
                        && video.getBanner().isEmpty()
                        && video.getThumbnail().isEmpty()
                        && video.getThumbnailHalf().isEmpty()
        ));
    }

    @Test
    void givenANullTitle_whenCallsCreateVideo_thenReturnDomainException() {
        final var expectedErrorMessage = "'title' should not be null";
        final var command = CreateVideoCommand.with(
                null,
                Fixture.Videos.description(),
                Fixture.year().getValue(),
                Fixture.Videos.duration(),
                Fixture.Videos.rating().getName(),
                Fixture.bool(),
                Fixture.bool(),
                asString(Set.<CategoryID>of()),
                asString(Set.<GenreID>of()),
                asString(Set.<CastMemberID>of()),
                null,
                null,
                null,
                null,
                null
        );

        final var response = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, response.getErrors().get(0).message());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(videoGateway, times(0)).create(any());
    }

    @Test
    void givenAEmptyTitle_whenCallsCreateVideo_thenReturnDomainException() {
        final var expectedErrorMessage = "'title' should not be empty";
        final var command = CreateVideoCommand.with(
                "",
                Fixture.Videos.description(),
                Fixture.year().getValue(),
                Fixture.Videos.duration(),
                Fixture.Videos.rating().getName(),
                Fixture.bool(),
                Fixture.bool(),
                asString(Set.<CategoryID>of()),
                asString(Set.<GenreID>of()),
                asString(Set.<CastMemberID>of()),
                null,
                null,
                null,
                null,
                null
        );

        final var response = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, response.getErrors().get(0).message());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(videoGateway, times(0)).create(any());
    }

    @Test
    void givenANullRating_whenCallsCreateVideo_thenReturnDomainException() {
        final var expectedErrorMessage = "'rating' should not be null";
        final var command = CreateVideoCommand.with(
                Fixture.Videos.title(),
                Fixture.Videos.description(),
                Fixture.year().getValue(),
                Fixture.Videos.duration(),
                null,
                Fixture.bool(),
                Fixture.bool(),
                asString(Set.<CategoryID>of()),
                asString(Set.<GenreID>of()),
                asString(Set.<CastMemberID>of()),
                null,
                null,
                null,
                null,
                null
        );

        final var response = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, response.getErrors().get(0).message());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(videoGateway, times(0)).create(any());
    }

    @Test
    void givenANullLaunchYear_whenCallsCreateVideo_thenReturnDomainException() {
        final var expectedErrorMessage = "'launchedAt' should not be null";
        final var command = CreateVideoCommand.with(
                Fixture.Videos.title(),
                Fixture.Videos.description(),
                null,
                Fixture.Videos.duration(),
                Fixture.Videos.rating().getName(),
                Fixture.bool(),
                Fixture.bool(),
                asString(Set.<CategoryID>of()),
                asString(Set.<GenreID>of()),
                asString(Set.<CastMemberID>of()),
                null,
                null,
                null,
                null,
                null
        );

        final var response = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, response.getErrors().get(0).message());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(videoGateway, times(0)).create(any());
    }

    @Test
    void givenAValidCommand_whenCallsCreateVideoAndSomeCategoriesDoesNotExists_thenReturnDomainException() {
        final var categoryID = CategoryID.from("123");
        final var expectedErrorMessage = "Some categories could not be found: %s".formatted(categoryID.getValue());
        final var command = CreateVideoCommand.with(
                Fixture.Videos.title(),
                Fixture.Videos.description(),
                Fixture.year().getValue(),
                Fixture.Videos.duration(),
                Fixture.Videos.rating().getName(),
                Fixture.bool(),
                Fixture.bool(),
                asString(Set.of(categoryID)),
                asString(Set.<GenreID>of()),
                asString(Set.<CastMemberID>of()),
                null,
                null,
                null,
                null,
                null
        );

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>());

        final var response = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, response.getErrors().get(0).message());
        verify(categoryGateway, times(1)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(videoGateway, times(0)).create(any());
    }

    @Test
    void givenAValidCommand_whenCallsCreateVideoAndSomeGenresDoesNotExists_thenReturnDomainException() {
        final var genreID = GenreID.from("123");
        final var expectedErrorMessage = "Some genres could not be found: %s".formatted(genreID.getValue());
        final var command = CreateVideoCommand.with(
                Fixture.Videos.title(),
                Fixture.Videos.description(),
                Fixture.year().getValue(),
                Fixture.Videos.duration(),
                Fixture.Videos.rating().getName(),
                Fixture.bool(),
                Fixture.bool(),
                asString(Set.<CategoryID>of()),
                asString(Set.of(genreID)),
                asString(Set.<CastMemberID>of()),
                null,
                null,
                null,
                null,
                null
        );

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>());

        final var response = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, response.getErrors().get(0).message());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
        verify(genreGateway, times(1)).existsByIds(any());
        verify(videoGateway, times(0)).create(any());
    }

    @Test
    void givenAValidCommand_whenCallsCreateVideoAndSomeCastMembersDoesNotExists_thenReturnDomainException() {
        final var memberID = CastMemberID.from("123");
        final var expectedErrorMessage = "Some cast members could not be found: %s".formatted(memberID.getValue());
        final var command = CreateVideoCommand.with(
                Fixture.Videos.title(),
                Fixture.Videos.description(),
                Fixture.year().getValue(),
                Fixture.Videos.duration(),
                Fixture.Videos.rating().getName(),
                Fixture.bool(),
                Fixture.bool(),
                asString(Set.<CategoryID>of()),
                asString(Set.<CastMemberID>of()),
                asString(Set.of(memberID)),
                null,
                null,
                null,
                null,
                null
        );

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>());

        final var response = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, response.getErrors().get(0).message());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(1)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(videoGateway, times(0)).create(any());
    }

    @Test
    void givenAValidCommand_whenCallsCreateVideoThrowsException_shouldCallClearResources() {
        final var expectedErrorMessage = "An error on create video was observed [videoId:";
        final var command = CreateVideoCommand.with(
                Fixture.Videos.title(),
                Fixture.Videos.description(),
                Fixture.year().getValue(),
                Fixture.Videos.duration(),
                Fixture.Videos.rating().getName(),
                Fixture.bool(),
                Fixture.bool(),
                asString(Set.<CategoryID>of()),
                asString(Set.<CastMemberID>of()),
                asString(Set.<CastMemberID>of()),
                null,
                null,
                null,
                null,
                null
        );

        when(videoGateway.create(any()))
                .thenThrow(new RuntimeException("Internal Server Error"));

        final var response = Assertions.assertThrows(
                InternalErrorException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertTrue(response.getMessage().startsWith(expectedErrorMessage));
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
