package com.fullcycle.admin.catalog.application.video.update;

import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.Fixture;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.InternalErrorException;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.utils.IdUtils;
import com.fullcycle.admin.catalog.domain.video.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

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
    void givenAValidCommand_whenCallsUpdateVideo_thenReturnVideoId() {
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

    @Test
    void givenAValidCommandWithoutCategories_whenCallsUpdateVideo_thenReturnVideoId() {
        final var video = Fixture.Videos.systemDesign();
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
        final Resource expectedVideo = Fixture.Videos.resource(Resource.Type.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(Resource.Type.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(Resource.Type.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(Resource.Type.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(Resource.Type.THUMBNAIL_HALF);

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
                .thenReturn(Optional.of(Video.with(video)));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedMembers));

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        mockImageMedia();
        mockAudioVideoMedia();

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

    @Test
    void givenAValidCommandWithoutGenres_whenCallsUpdateVideo_thenReturnVideoId() {
        final var video = Fixture.Videos.systemDesign();
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
        final Resource expectedVideo = Fixture.Videos.resource(Resource.Type.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(Resource.Type.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(Resource.Type.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(Resource.Type.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(Resource.Type.THUMBNAIL_HALF);

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
                .thenReturn(Optional.of(Video.with(video)));

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedMembers));

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        mockImageMedia();
        mockAudioVideoMedia();

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

    @Test
    void givenAValidCommandWithoutCastMembers_whenCallsUpdateVideo_thenReturnVideoId() {
        final var video = Fixture.Videos.systemDesign();
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
        final Resource expectedVideo = Fixture.Videos.resource(Resource.Type.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(Resource.Type.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(Resource.Type.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(Resource.Type.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(Resource.Type.THUMBNAIL_HALF);

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
                .thenReturn(Optional.of(Video.with(video)));

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        mockImageMedia();
        mockAudioVideoMedia();

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

    @Test
    void givenAValidCommandWithoutResources_whenCallsUpdateVideo_thenReturnVideoId() {
        final var video = Fixture.Videos.systemDesign();
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
                null,
                null,
                null,
                null,
                null
        );
        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(video)));

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedMembers));

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
                        && updatedVideo.getVideo().isEmpty()
                        && updatedVideo.getTrailer().isEmpty()
                        && updatedVideo.getBanner().isEmpty()
                        && updatedVideo.getThumbnail().isEmpty()
                        && updatedVideo.getThumbnailHalf().isEmpty()
        ));
    }

    @Test
    void givenANullTitle_whenCallsUpdateVideo_thenReturnDomainException() {
        final var expectedErrorMessage = "'title' should not be null";
        final var video = Fixture.Videos.systemDesign();
        final var command = UpdateVideoCommand.with(
                "123",
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
        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(video)));

        final var response = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, response.getErrors().get(0).message());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(videoGateway, times(0)).update(any());
    }

    @Test
    void givenAEmptyTitle_whenCallsUpdateVideo_thenReturnDomainException() {
        final var expectedErrorMessage = "'title' should not be empty";
        final var video = Fixture.Videos.systemDesign();
        final var command = UpdateVideoCommand.with(
                "123",
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
        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(video)));

        final var response = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, response.getErrors().get(0).message());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(videoGateway, times(0)).update(any());
    }

    @Test
    void givenANullRating_whenCallsUpdateVideo_thenReturnDomainException() {
        final var expectedErrorMessage = "'rating' should not be null";
        final var video = Fixture.Videos.systemDesign();
        final var command = UpdateVideoCommand.with(
                "123",
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
        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(video)));

        final var response = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, response.getErrors().get(0).message());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(videoGateway, times(0)).update(any());
    }

    @Test
    void givenANullLaunchYear_whenCallsUpdateVideo_thenReturnDomainException() {
        final var expectedErrorMessage = "'launchedAt' should not be null";
        final var video = Fixture.Videos.systemDesign();
        final var command = UpdateVideoCommand.with(
                "123",
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
        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(video)));

        final var response = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, response.getErrors().get(0).message());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(videoGateway, times(0)).update(any());
    }

    @Test
    void givenAValidCommand_whenCallsUpdateVideoAndSomeCategoriesDoesNotExists_thenReturnDomainException() {
        final var video = Fixture.Videos.systemDesign();
        final var categoryID = CategoryID.from("123");
        final var expectedErrorMessage = "Some categories could not be found: %s".formatted(categoryID.getValue());
        final var command = UpdateVideoCommand.with(
                video.getId().getValue(),
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
        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(video)));

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
        verify(videoGateway, times(0)).update(any());
    }

    @Test
    void givenAValidCommand_whenCallsUpdateVideoAndSomeGenresDoesNotExists_thenReturnDomainException() {
        final var video = Fixture.Videos.systemDesign();
        final var genreID = GenreID.from("123");
        final var expectedErrorMessage = "Some genres could not be found: %s".formatted(genreID.getValue());
        final var command = UpdateVideoCommand.with(
                video.getId().getValue(),
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

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(video)));

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
        verify(videoGateway, times(0)).update(any());
    }

    @Test
    void givenAValidCommand_whenCallsUpdateVideoAndSomeCastMembersDoesNotExists_thenReturnDomainException() {
        final var video = Fixture.Videos.systemDesign();
        final var memberID = CastMemberID.from("123");
        final var expectedErrorMessage = "Some cast members could not be found: %s".formatted(memberID.getValue());
        final var command = UpdateVideoCommand.with(
                video.getId().getValue(),
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
        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(video)));

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
        verify(videoGateway, times(0)).update(any());
    }

    @Test
    void givenAValidCommand_whenCallsUpdateVideoThrowsException_shouldCallClearResources() {
        final var expectedErrorMessage = "An error on update video was observed [videoId:";
        final var video = Fixture.Videos.systemDesign();
        final var command = UpdateVideoCommand.with(
                "123",
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
        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(video)));

        when(videoGateway.update(any()))
                .thenThrow(new RuntimeException("Internal Server Error"));

        final var response = Assertions.assertThrows(
                InternalErrorException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertTrue(response.getMessage().startsWith(expectedErrorMessage));
        verify(mediaResourceGateway).clearResource(any());
    }

    private void mockAudioVideoMedia() {
        when(mediaResourceGateway.storeAudioVideo(any(), any()))
                .thenAnswer(t -> {
                    final var resource = t.getArgument(1, Resource.class);
                    return AudioVideoMedia.with(
                            IdUtils.uuid(),
                            resource.name(),
                            "/img"
                    );
                });
    }

    private void mockImageMedia() {
        when(mediaResourceGateway.storeImage(any(), any()))
                .thenAnswer(t -> {
                    final var resource = t.getArgument(1, Resource.class);
                    return ImageMedia.with(IdUtils.uuid(), resource.name(), "/img");
                });
    }
}
