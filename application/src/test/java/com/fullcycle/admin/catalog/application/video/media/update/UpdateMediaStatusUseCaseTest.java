package com.fullcycle.admin.catalog.application.video.media.update;

import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.Fixture;
import com.fullcycle.admin.catalog.domain.video.MediaStatus;
import com.fullcycle.admin.catalog.domain.video.Video;
import com.fullcycle.admin.catalog.domain.video.VideoGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpdateMediaStatusUseCaseTest extends UseCaseTest {
    @InjectMocks
    private DefaultUpdateMediaStatusUseCase useCase;

    @Mock
    private VideoGateway videoGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway);
    }

    @Test
    void givenCommandForVideo_whenIsValid_thenUpdateStatusAndEncodedLocation() {
        final var expectedStatus = MediaStatus.COMPLETED;
        final var expectedFolder = "encoded_media";
        final var expectedFilename = "filename.mp4";
        final var expectedMedia = Fixture.Videos.audioVideo();

        final var video = Fixture.Videos.systemDesign().updateVideo(expectedMedia);
        final var expectedVideoId = video.getId();
        final var command = UpdateMediaStatusCommand.with(
                expectedVideoId.getValue(),
                expectedMedia.id(),
                expectedStatus,
                expectedFolder,
                expectedFilename
        );

        when(videoGateway.findById(expectedVideoId))
                .thenReturn(Optional.of(video));

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        useCase.execute(command);

        verify(videoGateway, times(1)).findById(eq(expectedVideoId));

        final var captor = ArgumentCaptor.forClass(Video.class);
        verify(videoGateway, times(1)).update(captor.capture());

        final var updatedMedia = captor.getValue().getVideo().get();
        Assertions.assertEquals(expectedMedia.id(), updatedMedia.id());
        Assertions.assertEquals(expectedMedia.rawLocation(), updatedMedia.rawLocation());
        Assertions.assertEquals(expectedMedia.checksum(), updatedMedia.checksum());
        Assertions.assertEquals(expectedStatus, updatedMedia.status());
        Assertions.assertEquals(expectedFolder.concat("/").concat(expectedFilename), updatedMedia.encodedPath());
    }

    @Test
    void givenCommandForVideo_whenIsValidForProcessing_thenUpdateStatusAndEncodedLocation() {
        final var expectedStatus = MediaStatus.PROCESSING;
        final var expectedMedia = Fixture.Videos.audioVideo();
        final var video = Fixture.Videos.systemDesign().updateVideo(expectedMedia);
        final var expectedVideoId = video.getId();
        final var command = UpdateMediaStatusCommand.with(
                expectedVideoId.getValue(),
                expectedMedia.id(),
                expectedStatus,
                null,
                null
        );

        when(videoGateway.findById(expectedVideoId))
                .thenReturn(Optional.of(video));

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        useCase.execute(command);

        verify(videoGateway, times(1)).findById(eq(expectedVideoId));

        final var captor = ArgumentCaptor.forClass(Video.class);
        verify(videoGateway, times(1)).update(captor.capture());

        final var updatedMedia = captor.getValue().getVideo().get();
        Assertions.assertEquals(expectedMedia.id(), updatedMedia.id());
        Assertions.assertEquals(expectedMedia.rawLocation(), updatedMedia.rawLocation());
        Assertions.assertEquals(expectedMedia.checksum(), updatedMedia.checksum());
        Assertions.assertEquals(expectedStatus, updatedMedia.status());
        Assertions.assertTrue(updatedMedia.encodedPath().isEmpty());
    }

    @Test
    void givenCommandForTrailer_whenIsValid_thenUpdateStatusAndEncodedLocation() {
        final var expectedStatus = MediaStatus.COMPLETED;
        final var expectedFolder = "encoded_media";
        final var expectedFilename = "filename.mp4";
        final var expectedMedia = Fixture.Videos.audioTrailer();
        final var video = Fixture.Videos.systemDesign().updateTrailer(expectedMedia);
        final var expectedVideoId = video.getId();
        final var command = UpdateMediaStatusCommand.with(
                expectedVideoId.getValue(),
                expectedMedia.id(),
                expectedStatus,
                expectedFolder,
                expectedFilename
        );

        when(videoGateway.findById(expectedVideoId))
                .thenReturn(Optional.of(video));

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        useCase.execute(command);

        verify(videoGateway, times(1)).findById(eq(expectedVideoId));

        final var captor = ArgumentCaptor.forClass(Video.class);
        verify(videoGateway, times(1)).update(captor.capture());

        final var updatedMedia = captor.getValue().getTrailer().get();
        Assertions.assertEquals(expectedMedia.id(), updatedMedia.id());
        Assertions.assertEquals(expectedMedia.rawLocation(), updatedMedia.rawLocation());
        Assertions.assertEquals(expectedMedia.checksum(), updatedMedia.checksum());
        Assertions.assertEquals(expectedStatus, updatedMedia.status());
        Assertions.assertEquals(expectedFolder.concat("/").concat(expectedFilename), updatedMedia.encodedPath());
    }

    @Test
    void givenCommandForTrailer_whenIsValidForProcessing_thenUpdateStatusAndEncodedLocation() {
        final var expectedStatus = MediaStatus.PROCESSING;
        final var expectedMedia = Fixture.Videos.audioTrailer();
        final var video = Fixture.Videos.systemDesign().updateTrailer(expectedMedia);
        final var expectedVideoId = video.getId();
        final var command = UpdateMediaStatusCommand.with(
                expectedVideoId.getValue(),
                expectedMedia.id(),
                expectedStatus,
                null,
                null
        );

        when(videoGateway.findById(expectedVideoId))
                .thenReturn(Optional.of(video));

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        useCase.execute(command);

        verify(videoGateway, times(1)).findById(eq(expectedVideoId));

        final var captor = ArgumentCaptor.forClass(Video.class);
        verify(videoGateway, times(1)).update(captor.capture());

        final var updatedMedia = captor.getValue().getTrailer().get();
        Assertions.assertEquals(expectedMedia.id(), updatedMedia.id());
        Assertions.assertEquals(expectedMedia.rawLocation(), updatedMedia.rawLocation());
        Assertions.assertEquals(expectedMedia.checksum(), updatedMedia.checksum());
        Assertions.assertEquals(expectedStatus, updatedMedia.status());
        Assertions.assertTrue(updatedMedia.encodedPath().isEmpty());
    }

    @Test
    void givenCommandForTrailer_whenIsInvalid_thenDoNothing() {
        final var expectedFolder = "encoded_media";
        final var expectedFilename = "filename.mp4";
        final var expectedMedia = Fixture.Videos.audioTrailer();
        final var video = Fixture.Videos.systemDesign().updateTrailer(expectedMedia);
        final var expectedVideoId = video.getId();
        final var command = UpdateMediaStatusCommand.with(
                expectedVideoId.getValue(),
                "bla",
                MediaStatus.COMPLETED,
                expectedFolder,
                expectedFilename
        );
        when(videoGateway.findById(expectedVideoId))
                .thenReturn(Optional.of(video));

        this.useCase.execute(command);

        verify(videoGateway, times(0)).update(any());
    }
}
