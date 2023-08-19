package com.fullcycle.admin.catalog.application.genre.retrieve.get;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

@IntegrationTest
class GetGenreByIdUseCaseIT {
    @Autowired
    private GetGenreByIdUseCase useCase;

    @SpyBean
    private GenreGateway genreGateway;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidId_whenCallsGetGenre_thenReturnGenre() {
        final var expectedName = "Terror";
        final var expectedIsActive = true;
        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var series = categoryGateway.create(Category.newCategory("Séries", null, true));
        final var expectedCategories = List.of(filmes.getId(), series.getId());
        final var genre = genreGateway.create(Genre.newGenre("Terror", true).addCategories(expectedCategories));
        final var expectedId = genre.getId();

        final var result = useCase.execute(expectedId.getValue());

        Assertions.assertEquals(expectedId.getValue(), result.id());
        Assertions.assertEquals(expectedName, result.name());
        Assertions.assertEquals(expectedIsActive, result.isActive());
        Assertions.assertTrue(asString(expectedCategories).containsAll(result.categories()));
        Assertions.assertEquals(genre.getCreatedAt(), result.createdAt());
        Assertions.assertEquals(genre.getUpdatedAt(), result.updatedAt());
        Assertions.assertEquals(genre.getDeletedAt(), result.deletedAt());
    }

    @Test
    void givenAValidId_whenCallsGetGenreAndDoesNotExists_thenReturnNotFound() {
        final var expectedId = GenreID.from("123");
        final var expectedErrorMessage = "Genre with ID 123 was not found";

        final var exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> useCase.execute(expectedId.getValue())
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }

    private List<String> asString(final List<CategoryID> ids) {
        return ids.stream()
                .map(CategoryID::getValue)
                .toList();
    }
}
