package com.fullcycle.admin.catalog.application.genre.retrieve.list;

import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class ListGenreUseCaseTest extends UseCaseTest {

    @Mock
    private GenreGateway genreGateway;

    @InjectMocks
    private DefaultListGenreUseCase useCase;


    @Override
    protected List<Object> getMocks() {
        return List.of(genreGateway);
    }

    @Test
    void givenAValidQuery_whenCallsListGenre_thenReturnGenres() {
        final var genres = List.of(
            Genre.newGenre("Ação", true),
            Genre.newGenre("Aventura", true)
        );
        final var expectedItems = genres.stream()
                .map(GenreListOutput::from)
                .toList();
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;
        final var expectedPagination = new Pagination<>(expectedPage, expectedPerPage, genres.size(), genres);
        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        when(genreGateway.findAll(any())).thenReturn(expectedPagination);

        final var response = useCase.execute(query);

        Assertions.assertEquals(expectedItems, response.items());
        Assertions.assertEquals(expectedPage, response.currentPage());
        Assertions.assertEquals(expectedPerPage, response.perPage());
        Assertions.assertEquals(expectedTotal, response.total());
    }

    @Test
    void givenAValidQuery_whenCallsListGenreAndResultIsEmpty_thenReturnGenres() {
        final var genres = List.<Genre>of();
        final var expectedItems = List.<GenreListOutput>of();
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;
        final var expectedPagination = new Pagination<>(expectedPage, expectedPerPage, expectedTotal, genres);
        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        when(genreGateway.findAll(eq(query))).thenReturn(expectedPagination);

        final var response = useCase.execute(query);

        Assertions.assertEquals(expectedItems, response.items());
        Assertions.assertEquals(expectedPage, response.currentPage());
        Assertions.assertEquals(expectedPerPage, response.perPage());
        Assertions.assertEquals(expectedTotal, response.total());
    }

    @Test
    void givenAValidQuery_whenCallsListGenreAndGatewayThrowsRandomError_thenReturnException() {
        final var expectedErrorMessage = "Gateway error";
        final var query = new SearchQuery(0, 10, "", "createdAt", "asc");
        when(genreGateway.findAll(any()))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var response = Assertions.assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(query)
        );

        Assertions.assertEquals(expectedErrorMessage, response.getMessage());
        Mockito.verify(genreGateway, times(1)).findAll(eq(query));
    }
}
