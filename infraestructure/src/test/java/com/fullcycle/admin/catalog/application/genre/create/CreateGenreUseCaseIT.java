package com.fullcycle.admin.catalog.application.genre.create;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.infraestructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@IntegrationTest
class CreateGenreUseCaseIT {

    @Autowired
    private CreateGenreUseCase useCase;

    @SpyBean
    private GenreGateway genreGateway;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void givenAValidCommand_whenCallsCreateGenre_thenReturnGenreId() {
        final var category = categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var expectedName = "Terror";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(category.getId());
        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var result = useCase.execute(command);

        Assertions.assertNotNull(result.id());

        final var persistedGenre = genreRepository.findById(result.id()).get();
        Assertions.assertEquals(expectedName, persistedGenre.getName());
        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertEquals(expectedCategories, persistedGenre.getCategoryIDs());
        Assertions.assertNotNull(persistedGenre.getCreatedAt());
        Assertions.assertNotNull(persistedGenre.getUpdatedAt());
        Assertions.assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    void givenAValidCommandWithoutCategories_whenCallsCreateGenre_thenReturnGenreId() {
        final var expectedName = "Terror";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var result = useCase.execute(command);

        Assertions.assertNotNull(result.id());

        final var persistedGenre = genreRepository.findById(result.id()).get();
        Assertions.assertEquals(expectedName, persistedGenre.getName());
        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertEquals(expectedCategories, persistedGenre.getCategoryIDs());
        Assertions.assertNotNull(persistedGenre.getCreatedAt());
        Assertions.assertNotNull(persistedGenre.getUpdatedAt());
        Assertions.assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    void givenAValidCommandWithInactiveGenre_whenCallsCreateGenre_thenReturnGenreId() {
        final var expectedIsActive = false;
        final var command = CreateGenreCommand.with("Terror", expectedIsActive, asString(List.of()));

        final var result = useCase.execute(command);

        Assertions.assertNotNull(result.id());

        final var persistedGenre = genreRepository.findById(result.id()).get();
        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertNotNull(persistedGenre.getDeletedAt());
    }

    @Test
    void givenAInvalidEmptyName_whenCallsCreateGenre_thenReturnDomainException() {
        final var invalidName = "";
        final var expectedErrorMessage = "'name' should not be empty";
        final var command = CreateGenreCommand.with(invalidName, true, asString(List.of()));

        final var error = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, error.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidNullName_whenCallsCreateGenre_thenReturnDomainException() {
        final String invalidName = null;
        final var expectedErrorMessage = "'name' should not be null";
        final var command = CreateGenreCommand.with(invalidName, true, asString(List.of()));

        final var error = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, error.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidName_whenCallsCreateGenreAndSomeCategoriesDoesNotExists_thenReturnDomainException(){
        final String invalidName = null;
        final var invalidCategories = List.of(
                CategoryID.from("456"),
                CategoryID.from("789")
        );
        final var expectedErrorMessageOne = "Some categories could not be found: 456, 789";
        final var expectedErrorMessageTwo = "'name' should not be null";
        final var command = CreateGenreCommand.with(invalidName, true, asString(invalidCategories));

        final var error = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessageOne, error.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, error.getErrors().get(1).message());

        Mockito.verify(categoryGateway, times(1)).existsByIds(any());
        Mockito.verify(genreGateway, times(0)).create(any());
    }

    private List<String> asString(final List<CategoryID> categoryIDS) {
        return categoryIDS.stream()
                .map(CategoryID::getValue)
                .toList();
    }
}
