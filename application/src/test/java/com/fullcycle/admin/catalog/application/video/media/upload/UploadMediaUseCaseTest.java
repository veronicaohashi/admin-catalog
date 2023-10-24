package com.fullcycle.admin.catalog.application.video.media.upload;

import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.Fixture;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalog.domain.video.VideoGateway;
import com.fullcycle.admin.catalog.domain.video.VideoMediaType;
import com.fullcycle.admin.catalog.domain.video.VideoResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UploadMediaUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultUploadMediaUseCase useCase;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Mock
    private VideoGateway videoGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(mediaResourceGateway, videoGateway);
    }

    @Test
    void givenCmdToUpload_whenIsValid_thenUpdateVideoMediaAndPersistIt() {
        final var video = Fixture.video();
        final var expectedVideoId = video.getId();
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.videoResource();
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
        final var expectedMedia = Fixture.Videos.audioVideo();
        final var command = UploadMediaCommand.with(expectedVideoId.getValue(), expectedVideoResource);
        when(videoGateway.findById(expectedVideoId))
                .thenReturn(Optional.of(video));

        when(mediaResourceGateway.storeAudioVideo(expectedVideoId, expectedVideoResource))
                .thenReturn(expectedMedia);

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var result = useCase.execute(command);

        Assertions.assertEquals(expectedType, result.mediaType());
        Assertions.assertEquals(expectedVideoId.getValue(), result.videoId());

        Mockito.verify(videoGateway, times(1)).findById(eq(expectedVideoId));
        Mockito.verify(mediaResourceGateway, times(1)).storeAudioVideo(eq(expectedVideoId), eq(expectedVideoResource));
        verify(videoGateway, times(1)).update(argThat(updatedVideo ->
                Objects.equals(expectedMedia, updatedVideo.getVideo().get())));

    }

    @Test
    void givenCmdToUpload_whenIsValid_thenUpdateTrailerMediaAndPersistIt() {
        final var video = Fixture.video();
        final var expectedVideoId = video.getId();
        final var expectedType = VideoMediaType.TRAILER;
        final var expectedResource = Fixture.Videos.trailerResource();
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
        final var expectedMedia = Fixture.Videos.audioTrailer();
        final var command = UploadMediaCommand.with(expectedVideoId.getValue(), expectedVideoResource);
        when(videoGateway.findById(expectedVideoId))
                .thenReturn(Optional.of(video));

        when(mediaResourceGateway.storeAudioVideo(expectedVideoId, expectedVideoResource))
                .thenReturn(expectedMedia);

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var result = useCase.execute(command);

        Assertions.assertEquals(expectedType, result.mediaType());
        Assertions.assertEquals(expectedVideoId.getValue(), result.videoId());

        Mockito.verify(videoGateway, times(1)).findById(eq(expectedVideoId));
        Mockito.verify(mediaResourceGateway, times(1)).storeAudioVideo(eq(expectedVideoId), eq(expectedVideoResource));
        verify(videoGateway, times(1)).update(argThat(updatedVideo ->
                Objects.equals(expectedMedia, updatedVideo.getTrailer().get())));
    }

    @Test
    void givenCmdToUpload_whenIsValid_thenUpdateBannerMediaAndPersistIt() {
        final var video = Fixture.video();
        final var expectedVideoId = video.getId();
        final var expectedType = VideoMediaType.BANNER;
        final var expectedResource = Fixture.Videos.bannerResource();
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
        final var expectedMedia = Fixture.Videos.imageBanner();
        final var command = UploadMediaCommand.with(expectedVideoId.getValue(), expectedVideoResource);
        when(videoGateway.findById(expectedVideoId))
                .thenReturn(Optional.of(video));

        when(mediaResourceGateway.storeImage(expectedVideoId, expectedVideoResource))
                .thenReturn(expectedMedia);

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var result = useCase.execute(command);

        Assertions.assertEquals(expectedType, result.mediaType());
        Assertions.assertEquals(expectedVideoId.getValue(), result.videoId());

        Mockito.verify(videoGateway, times(1)).findById(eq(expectedVideoId));
        Mockito.verify(mediaResourceGateway, times(1)).storeImage(eq(expectedVideoId), eq(expectedVideoResource));
        verify(videoGateway, times(1)).update(argThat(updatedVideo ->
                Objects.equals(expectedMedia, updatedVideo.getBanner().get())));
    }

    @Test
    void givenCmdToUpload_whenIsValid_thenUpdateThumbnailMediaAndPersistIt() {
        final var video = Fixture.video();
        final var expectedVideoId = video.getId();
        final var expectedType = VideoMediaType.THUMBNAIL;
        final var expectedResource = Fixture.Videos.thumbnailResource();
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
        final var expectedMedia = Fixture.Videos.imageThumbnail();
        final var command = UploadMediaCommand.with(expectedVideoId.getValue(), expectedVideoResource);
        when(videoGateway.findById(expectedVideoId))
                .thenReturn(Optional.of(video));

        when(mediaResourceGateway.storeImage(expectedVideoId, expectedVideoResource))
                .thenReturn(expectedMedia);

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var result = useCase.execute(command);

        Assertions.assertEquals(expectedType, result.mediaType());
        Assertions.assertEquals(expectedVideoId.getValue(), result.videoId());

        Mockito.verify(videoGateway, times(1)).findById(eq(expectedVideoId));
        Mockito.verify(mediaResourceGateway, times(1)).storeImage(eq(expectedVideoId), eq(expectedVideoResource));
        verify(videoGateway, times(1)).update(argThat(updatedVideo ->
                Objects.equals(expectedMedia, updatedVideo.getThumbnail().get())));
    }


    @Test
    void givenCmdToUpload_whenIsValid_thenUpdateThumbnailHalfMediaAndPersistIt() {
        final var video = Fixture.video();
        final var expectedVideoId = video.getId();
        final var expectedType = VideoMediaType.THUMBNAIL_HALF;
        final var expectedResource = Fixture.Videos.thumbnailHalfResource();
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
        final var expectedMedia = Fixture.Videos.imageThumbnailHalf();
        final var command = UploadMediaCommand.with(expectedVideoId.getValue(), expectedVideoResource);
        when(videoGateway.findById(expectedVideoId))
                .thenReturn(Optional.of(video));

        when(mediaResourceGateway.storeImage(expectedVideoId, expectedVideoResource))
                .thenReturn(expectedMedia);

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var result = useCase.execute(command);

        Assertions.assertEquals(expectedType, result.mediaType());
        Assertions.assertEquals(expectedVideoId.getValue(), result.videoId());

        Mockito.verify(videoGateway, times(1)).findById(eq(expectedVideoId));
        Mockito.verify(mediaResourceGateway, times(1)).storeImage(eq(expectedVideoId), eq(expectedVideoResource));
        verify(videoGateway, times(1)).update(argThat(updatedVideo ->
                Objects.equals(expectedMedia, updatedVideo.getThumbnailHalf().get())));
    }

    @Test
    void givenCmdToUpload_whenVideoIsInvalid_thenReturnNotFound() {
        final var video = Fixture.video();
        final var expectedVideoId = video.getId();
        final var expectedType = VideoMediaType.THUMBNAIL_HALF;
        final var expectedResource = Fixture.Videos.thumbnailHalfResource();
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
        final var command = UploadMediaCommand.with(expectedVideoId.getValue(), expectedVideoResource);
        final var expectedErrorMessage = "Video with ID %s was not found".formatted(expectedVideoId.getValue());

        when(videoGateway.findById(any()))
                .thenReturn(Optional.empty());

        final var error = Assertions.assertThrows(
                NotFoundException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, error.getMessage());
    }
}
