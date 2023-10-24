package com.fullcycle.admin.catalog.application.video.delete;

import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.exception.InternalErrorException;
import com.fullcycle.admin.catalog.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalog.domain.video.VideoGateway;
import com.fullcycle.admin.catalog.domain.video.VideoID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.Mockito.*;

class DeleteVideoUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultDeleteVideoUseCase useCase;

    @Mock
    private VideoGateway videoGateway;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway, mediaResourceGateway);
    }

    @Test
    void givenAValidId_whenCallsDeleteVideo_thenDeleteIt() {
        final var id = VideoID.unique();
        doNothing().when(videoGateway).deleteById(id);
        doNothing().when(mediaResourceGateway).clearResource(id);

        useCase.execute(id.getValue());

        verify(videoGateway).deleteById(id);
        verify(mediaResourceGateway).clearResource(id);
    }

    @Test
    void givenAnInvalidId_whenCallsDeleteVideo_thenBeOk() {
        final var id = VideoID.from("1");
        doNothing().when(videoGateway).deleteById(id);

        useCase.execute(id.getValue());

        verify(videoGateway).deleteById(id);
    }

    @Test
    void givenAValidId_whenCallsDeleteVideoAndGatewayThrowsException_thenReceiveException() {
        final var id = VideoID.unique();
        doThrow(InternalErrorException.with("Error on delete video", new RuntimeException()))
                .when(videoGateway).deleteById(id);

        Assertions.assertThrows(
                InternalErrorException.class,
                () -> useCase.execute(id.getValue())
        );

        verify(videoGateway).deleteById(id);
    }
}
