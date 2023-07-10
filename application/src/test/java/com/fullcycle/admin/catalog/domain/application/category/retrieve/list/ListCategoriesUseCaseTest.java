package com.fullcycle.admin.catalog.domain.application.category.retrieve.list;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import io.vavr.API;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListCategoriesUseCaseTest {

    @InjectMocks
    private DefaultListCategoriesUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
    }

    @Test
    void givenAValidQuery_whenCallsListCategories_thenReturnCategories() {
        final var categories = List.of(
                Category.newCategory("Filmes", null, true),
                Category.newCategory("Series", null, true)
        );
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var query =
                new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        final var expectedPagination = new Pagination<>(expectedPage, expectedPerPage, categories.size(), categories);
        final var expectedResult = expectedPagination.map(CategoryListOutput::from);
        when(categoryGateway.findAll(eq(query))).thenReturn(expectedPagination);

        final var response = useCase.execute(query);

        Assertions.assertEquals(expectedResult, response);
        Assertions.assertEquals(expectedPage, response.currentPage());
        Assertions.assertEquals(expectedPerPage, response.perPage());
        Assertions.assertEquals(categories.size(), response.total());
    }


    @Test
    void givenAValidQuery_whenHasNoResults_thenReturnEmptyCategories(){
        final var categories = List.<Category>of();
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 0;
        final var query =
                new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        final var expectedPagination = new Pagination<>(expectedPage, expectedPerPage, categories.size(), categories);
        final var expectedResult = expectedPagination.map(CategoryListOutput::from);
        when(categoryGateway.findAll(eq(query))).thenReturn(expectedPagination);

        final var response = useCase.execute(query);

        Assertions.assertEquals(expectedItemsCount, response.items().size());
        Assertions.assertEquals(expectedResult, response);
        Assertions.assertEquals(expectedPage, response.currentPage());
        Assertions.assertEquals(expectedPerPage, response.perPage());
        Assertions.assertEquals(categories.size(), response.total());
    }

    @Test
    void givenAValidQuery_whenGatewayThrowsException_thenReturnException() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedErrorMessage = "Gateway error";
        final var query =
                new CategorySearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        when(categoryGateway.findAll(eq(query)))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var response = Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(query));

        Assertions.assertEquals(expectedErrorMessage, response.getMessage());
    }
}
