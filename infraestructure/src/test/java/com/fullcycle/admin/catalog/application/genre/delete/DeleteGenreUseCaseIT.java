package com.fullcycle.admin.catalog.application.genre.delete;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.infraestructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class DeleteGenreUseCaseIT {

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private DeleteGenreUseCase useCase;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void givenAValidGenreId_whenCallsDeleteGenre_thenDeleteGenre() {
        final var genre = genreGateway.create(Genre.newGenre("Terror", true));
        final var expectedId = genre.getId();

        Assertions.assertEquals(1, genreRepository.count());

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        Assertions.assertEquals(0, genreRepository.count());
    }

    @Test
    void givenAInvalidId_whenCallsDeleteGenre_thenBeOK() {
        final var expectedId = GenreID.from("123");

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));
    }
}
