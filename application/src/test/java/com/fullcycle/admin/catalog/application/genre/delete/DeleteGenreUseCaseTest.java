package com.fullcycle.admin.catalog.application.genre.delete;

import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class DeleteGenreUseCaseTest extends UseCaseTest {

    @Mock
    private GenreGateway genreGateway;

    @InjectMocks
    private DefaultDeleteGenreUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(genreGateway);
    }

    @Test
    void givenAValidGenreId_whenCallsDeleteGenre_thenDeleteGenre() {
        final var genre = Genre.newGenre("Terror", true);
        final var expectedId = genre.getId();

        doNothing().when(genreGateway).deleteById(eq(expectedId));

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        verify(genreGateway, times(1)).deleteById(eq(expectedId));
    }

    @Test
    void givenAInvalidId_whenCallsDeleteGenre_thenBeOK() {
        final var expectedId = GenreID.from("123");
        doNothing().when(genreGateway).deleteById(eq(expectedId));

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        verify(genreGateway, times(1)).deleteById(eq(expectedId));
    }

    @Test
    void givenAValidId_whenGatewayThrowsException_shouldReturnException() {
        final var genre = Genre.newGenre("Terror", true);
        final var expectedId = genre.getId();
        doThrow(new IllegalStateException("Gateway error"))
                .when(genreGateway).deleteById(eq(expectedId));

        Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        Mockito.verify(genreGateway, times(1)).deleteById(eq(expectedId));
    }
}
