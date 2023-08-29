package com.fullcycle.admin.catalog.application.genre.update;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.infraestructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
class UpdateGenreUseCaseIT {

    @Autowired
    private UpdateGenreUseCase useCase;

    @SpyBean
    private GenreGateway genreGateway;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void givenAValidCommand_whenCallsUpdateGenre_thenReturnGenreId() {
        final var genre = genreGateway.create(Genre.newGenre("Terror", true));
        final var expectedId = genre.getId().getValue();
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var command = UpdateGenreCommand.with(expectedId, expectedName, expectedIsActive, asString(expectedCategories));

        final var response = useCase.execute(command);

        Assertions.assertEquals(expectedId, response.id());

        final var updatedGenre = genreRepository.findById(genre.getId().getValue()).get();

        Assertions.assertEquals(expectedName, updatedGenre.getName());
        Assertions.assertEquals(expectedIsActive, updatedGenre.isActive());
        Assertions.assertTrue(expectedCategories.containsAll(updatedGenre.getCategoryIDs()));
        Assertions.assertEquals(genre.getCreatedAt(), updatedGenre.getCreatedAt());
        Assertions.assertTrue(genre.getUpdatedAt().isBefore(updatedGenre.getUpdatedAt()));
        Assertions.assertNull(updatedGenre.getDeletedAt());
    }

    @Test
    void givenAValidCommandWithCategories_whenCallsUpdateGenre_thenReturnGenreId() {
        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var series = categoryGateway.create(Category.newCategory("Séries", null, true));
        final var genre = genreGateway.create(Genre.newGenre("Terror", true));
        final var expectedId = genre.getId().getValue();
        final var expectedCategories = List.of(
                filmes.getId(), series.getId()
        );
        final var command = UpdateGenreCommand.with(expectedId, genre.getName(), genre.isActive(), asString(expectedCategories));

        final var response = useCase.execute(command);

        Assertions.assertEquals(expectedId, response.id());

        final var updatedGenre = genreRepository.findById(genre.getId().getValue()).get();

        Assertions.assertTrue(expectedCategories.containsAll(updatedGenre.getCategoryIDs()));
    }

    @Test
    void givenAValidCommandWithInactiveGenre_whenCallsUpdateGenre_thenReturnGenreId() {
        final var genre = genreGateway.create(Genre.newGenre("Terror", true));
        final var expectedId = genre.getId().getValue();
        final var expectedIsActive = false;
        final var command = UpdateGenreCommand.with(expectedId, genre.getName(), expectedIsActive, asString(List.of()));

        final var response = useCase.execute(command);

        Assertions.assertEquals(expectedId, response.id());

        final var updatedGenre = genreRepository.findById(genre.getId().getValue()).get();

        Assertions.assertEquals(expectedIsActive, updatedGenre.isActive());
        Assertions.assertNotNull(updatedGenre.getDeletedAt());
    }

    @Test
    void givenAInvalidEmptyName_whenCallsUpdateGenre_thenReturnDomainException() {
        final var genre = genreGateway.create(Genre.newGenre("Terror", true));
        final var invalidName = "";
        final var expectedId = genre.getId().getValue();
        final var expectedErrorMessage = "'name' should not be empty";
        final var command = UpdateGenreCommand.with(expectedId, invalidName, genre.isActive(), asString(List.of()));

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        verify(genreGateway, times(1)).findById(any());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).update(any());
    }

    @Test
    void givenAInvalidNullName_whenCallsUpdateGenre_thenReturnDomainException() {
        final var genre = genreGateway.create(Genre.newGenre("Terror", true));
        final String invalidName = null;
        final var expectedId = genre.getId().getValue();
        final var expectedErrorMessage = "'name' should not be null";
        final var command = UpdateGenreCommand.with(expectedId, invalidName, genre.isActive(), asString(List.of()));

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        verify(genreGateway, times(1)).findById(any());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).update(any());
    }

    @Test
    void givenAValidCommand_whenCallsUpdateGenreAndSomeCategoriesDoesNotExists_thenReturnNotificationException() {
        final var genre = genreGateway.create(Genre.newGenre("Terror", true));
        final var expectedId = genre.getId().getValue();
        final var invalidCategories = List.of(
                CategoryID.from("456"),
                CategoryID.from("789")
        );
        final var expectedErrorMessageOne = "Some categories could not be found: 456, 789";
        final var expectedErrorMessageTwo = "'name' should not be null";

        final var command = UpdateGenreCommand.with(expectedId, null, genre.isActive(), asString(invalidCategories));


        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessageOne, exception.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, exception.getErrors().get(1).message());
        verify(genreGateway, times(1)).findById(any());
        verify(categoryGateway, times(1)).existsByIds(any());
        verify(genreGateway, times(0)).update(any());
    }

    @Test
    void givenACommandWithInvalidID_whenCallsUpdateGenre_thenReturnNotFoundException() {
        final var expectedId = "123";
        final var command = UpdateGenreCommand.with(expectedId, "Terror", true, List.of());
        final var expectedErrorMessage = "Genre with ID 123 was not found";

        final var notification = Assertions.assertThrows(
                NotFoundException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, notification.getMessage());
        verify(genreGateway, times(1)).findById(any());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).update(any());
    }

    private List<String> asString(final List<CategoryID> categories) {
        return categories.stream()
                .map(CategoryID::getValue)
                .toList();
    }
}
