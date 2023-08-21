package com.fullcycle.admin.catalog.application.genre.retrieve.list;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.infraestructure.genre.persistance.GenreJpaEntity;
import com.fullcycle.admin.catalog.infraestructure.genre.persistance.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@IntegrationTest
class ListGenreUseCaseIT {
    @Autowired
    private ListGenreUseCase useCase;

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void givenAValidQuery_whenCallsListGenre_thenReturnGenres() {
        final var genres = List.of(
                Genre.newGenre("Ação", true),
                Genre.newGenre("Aventura", true)
        );

        genreRepository.saveAllAndFlush(genres.stream()
                .map(GenreJpaEntity::from)
                .toList());

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;
        final var expectedItems = genres.stream()
                .map(GenreListOutput::from)
                .toList();
        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var response = useCase.execute(query);

        Assertions.assertTrue(expectedItems.containsAll(response.items()));
        Assertions.assertEquals(expectedPage, response.currentPage());
        Assertions.assertEquals(expectedPerPage, response.perPage());
        Assertions.assertEquals(expectedTotal, response.total());
    }

    @Test
    void givenAValidQuery_whenCallsListGenreAndResultIsEmpty_thenReturnGenres() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;
        final var expectedItems = List.<GenreListOutput>of();
        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var response = useCase.execute(query);

        Assertions.assertTrue(expectedItems.containsAll(response.items()));
        Assertions.assertEquals(expectedPage, response.currentPage());
        Assertions.assertEquals(expectedPerPage, response.perPage());
        Assertions.assertEquals(expectedTotal, response.total());
    }
}
