package com.fullcycle.admin.catalog.domain.genre;

import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class GenreTest {

    @Test
    void givenAValidParams_whenCallNewGenre_thenInstantiateAGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = 0;

        final var genre = Genre.newGenre(expectedName, expectedIsActive);

        Assertions.assertNotNull(genre.getId());
        Assertions.assertEquals(expectedName, genre.getName());
        Assertions.assertEquals(expectedIsActive, genre.isActive());
        Assertions.assertEquals(expectedCategories, genre.getCategories().size());
        Assertions.assertNotNull(genre.getCreatedAt());
        Assertions.assertNotNull(genre.getUpdatedAt());
        Assertions.assertNull(genre.getDeletedAt());
    }

    @Test
    void givenAnInvalidNullName_whenCallNewGenre_thenReturnAnException() {
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> Genre.newGenre(expectedName, expectedIsActive)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyName_whenCallNewGenre_thenReturnAnException() {
        final var invalidName = " ";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be empty";

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> Genre.newGenre(invalidName, expectedIsActive)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNameLengthGreaterThan255_whenCallNewCategory_thenReturnAnException() {
        final var invalidName = """
                A certificação de metodologias que nos auxiliam a lidar com a crescente influência da mídia talvez venha
                 a ressaltar a relatividade do processo de comunicação como um todo. Todas estas questões, devidamente 
                 ponderadas, levantam dúvidas sobre se o surgimento do comércio virtual promove a alavancagem do sistema 
                 de participação geral.                                                                                     
                """;
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' must be between 3 and 255 character";

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> Genre.newGenre(invalidName, expectedIsActive)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    void givenAValidActiveGenre_whenCallDeactivate_thenReturnAGenreInactivated() {
        final var expectedIsActive = false;
        final var genre = Genre.newGenre("Ação", true);
        final var activeGenreUpdatedAt = genre.getUpdatedAt();

        genre.deactivate();

        Assertions.assertEquals(expectedIsActive, genre.isActive());
        Assertions.assertTrue(genre.getUpdatedAt().isAfter(activeGenreUpdatedAt));
        Assertions.assertNotNull(genre.getDeletedAt());
    }

    @Test
    void givenAValidInactiveGenre_whenCallActivate_thenReturnGenreActivated() {
        final var expectedIsActive = true;
        final var genre = Genre.newGenre("Ação", false);
        final var inactiveGenreUpdatedAt = genre.getUpdatedAt();

        genre.activate();

        Assertions.assertEquals(expectedIsActive, genre.isActive());
        Assertions.assertTrue(genre.getUpdatedAt().isAfter(inactiveGenreUpdatedAt));
        Assertions.assertNull(genre.getDeletedAt());
    }

    @Test
    void givenAValidGenre_whenCallUpdate_thenReturnGenreUpdated() {
        final var expectedName = "Terror";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(CategoryID.from("123"));
        final var genre = Genre.newGenre("Ação", false);
        final var genreUpdatedAt = genre.getUpdatedAt();

        genre.update(expectedName, expectedIsActive, expectedCategories);

        Assertions.assertNotNull(genre.getId());
        Assertions.assertEquals(expectedName, genre.getName());
        Assertions.assertEquals(expectedIsActive, genre.isActive());
        Assertions.assertEquals(expectedCategories, genre.getCategories());
        Assertions.assertNotNull(genre.getCreatedAt());
        Assertions.assertTrue(genre.getUpdatedAt().isAfter(genreUpdatedAt));
        Assertions.assertNull(genre.getDeletedAt());
    }

    @Test
    void givenAValidGenre_whenCallUpdateToInactive_thenReturnGenreUpdated() {
        final var name = "Terror";
        final var expectedIsActive = false;
        final var categories = List.of(CategoryID.from("123"));
        final var genre = Genre.newGenre(name, true);
        final var genreUpdatedAt = genre.getUpdatedAt();

        genre.update(name, expectedIsActive, categories);

        Assertions.assertEquals(expectedIsActive, genre.isActive());
        Assertions.assertTrue(genre.getUpdatedAt().isAfter(genreUpdatedAt));
        Assertions.assertNotNull(genre.getDeletedAt());
    }

    @Test
    void givenAValidGenre_whenCallUpdateWithEmptyName_thenReceiveNotificationException() {
        final var invalidName = " ";
        final var expectedErrorMessage = "'name' should not be empty";
        final var genre = Genre.newGenre("acao", false);

        final var exception = Assertions.assertThrows(NotificationException.class, () -> {
            genre.update(invalidName, true, List.of());
        });

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    void givenAValidGenre_whenCallUpdateWithNullName_thenReceiveNotificationException() {
        final String invalidName = null;
        final var expectedErrorMessage = "'name' should not be null";
        final var genre = Genre.newGenre("acao", false);

        final var actualException = Assertions.assertThrows(NotificationException.class, () -> {
            genre.update(invalidName, true, List.of());
        });

        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAValidEmptyCategoriesGenre_whenCallAddCategory_thenReceiveOK() {
        final var seriesID = CategoryID.from("123");
        final var moviesID = CategoryID.from("456");
        final var expectedCategories = List.of(seriesID, moviesID);
        final var genre = Genre.newGenre("acao", true);
        final var genreUpdatedAt = genre.getUpdatedAt();

        Assertions.assertEquals(0, genre.getCategories().size());

        genre.addCategory(seriesID);
        genre.addCategory(moviesID);

        Assertions.assertNotNull(genre.getId());
        Assertions.assertEquals(expectedCategories, genre.getCategories());
        Assertions.assertTrue(genre.getUpdatedAt().isAfter(genreUpdatedAt));
    }

    @Test
    void givenAInvalidNullAsCategoryID_whenCallAddCategory_thenReceiveOK() {
        final var expectedCategories = new ArrayList<CategoryID>();
        final var genre = Genre.newGenre("acao", true);
        final var genreUpdatedAt = genre.getUpdatedAt();

        Assertions.assertEquals(0, genre.getCategories().size());

        genre.addCategory(null);

        Assertions.assertEquals(expectedCategories, genre.getCategories());
        Assertions.assertNotNull(genre.getCreatedAt());
        Assertions.assertEquals(genreUpdatedAt, genre.getUpdatedAt());
        Assertions.assertNull(genre.getDeletedAt());
    }

    @Test
    void givenAValidGenreWithTwoCategories_whenCallRemoveCategory_thenReceiveOK() {
        final var seriesID = CategoryID.from("123");
        final var moviesID = CategoryID.from("456");
        final var expectedCategories = List.of(moviesID);
        final var genre = Genre.newGenre("acao", true);
        genre.update("acao", true, List.of(seriesID, moviesID));
        final var genreUpdatedAt = genre.getUpdatedAt();

        Assertions.assertEquals(2, genre.getCategories().size());

        genre.removeCategory(seriesID);

        Assertions.assertEquals(expectedCategories, genre.getCategories());
        Assertions.assertNotNull(genre.getCreatedAt());
        Assertions.assertTrue(genre.getUpdatedAt().isAfter(genreUpdatedAt));
        Assertions.assertNull(genre.getDeletedAt());
    }

    @Test
    void givenAnInvalidNullAsCategoryID_whenCallRemoveCategory_thenReceiveOK() {
        final var seriesID = CategoryID.from("123");
        final var moviesID = CategoryID.from("456");
        final var expectedCategories = List.of(seriesID, moviesID);
        final var genre = Genre.newGenre("acao", true);
        genre.update("acao", true, expectedCategories);
        final var genreUpdatedAt = genre.getUpdatedAt();

        Assertions.assertEquals(2, genre.getCategories().size());

        genre.removeCategory(null);

        Assertions.assertEquals(expectedCategories, genre.getCategories());
        Assertions.assertNotNull(genre.getCreatedAt());
        Assertions.assertEquals(genreUpdatedAt, genre.getUpdatedAt());
        Assertions.assertNull(genre.getDeletedAt());
    }
}