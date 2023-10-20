package com.fullcycle.admin.catalog.application.video.media.get;

import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.Fixture;
import com.fullcycle.admin.catalog.domain.exception.DomainException;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalog.domain.video.VideoID;
import com.fullcycle.admin.catalog.domain.video.VideoMediaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.Not;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class GetMediaUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultGetMediaUseCase useCase;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Override
    protected List<Object> getMocks() {
        return null;
    }

    @Test
    void givenVideoIdAndType_whenIsValidCmd_thenReturnResource() {
        final var expectedVideoId = VideoID.unique();
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.videoResource();
        final var command = GetMediaCommand.with(expectedVideoId.getValue(), expectedType.name());

        when(mediaResourceGateway.getResource(expectedVideoId, expectedType))
                .thenReturn(Optional.of(expectedResource));

        final var result = useCase.execute(command);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResource.name(), result.name());
        Assertions.assertEquals(expectedResource.content(), result.content());
        Assertions.assertEquals(expectedResource.contentType(), result.contentType());
    }

    @Test
    void givenVideoIdAndType_whenIsNotFound_thenReturnNotFoundException() {
        when(mediaResourceGateway.getResource(any(), any()))
                .thenReturn(Optional.empty());
        final var expectedErrorMessage = "Resource VIDEO not found for video 123";

        final var command = GetMediaCommand.with("123", "VIDEO");

        final var error = Assertions.assertThrows(
                NotFoundException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, error.getMessage());
    }

    @Test
    void givenVideoIdAndType_whenTypeDoesntExists_thenReturnNotFoundException() {
        final var expectedErrorMessage = "Media type INVALIDO doesn't exist";

        final var command = GetMediaCommand.with("123", "INVALIDO");

        final var error = Assertions.assertThrows(
                NotFoundException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, error.getMessage());
    }
}
