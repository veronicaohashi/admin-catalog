package com.fullcycle.admin.catalog.domain.genre;

import com.fullcycle.admin.catalog.domain.exception.DomainException;
import com.fullcycle.admin.catalog.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

        final var genre = Genre.newGenre(expectedName, expectedIsActive);

        final var exception = Assertions.assertThrows(
                DomainException.class,
                () -> genre.validate(new ThrowsValidationHandler())
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }


    @Test
    void givenAnInvalidEmptyName_whenCallNewGenre_thenReturnAnException() {
        final var invalidName = " ";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be empty";

        final var genre = Genre.newGenre(invalidName, expectedIsActive);

        final var exception = Assertions.assertThrows(
                DomainException.class,
                () -> genre.validate(new ThrowsValidationHandler())
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

        final var genre = Genre.newGenre(invalidName, expectedIsActive);

        final var exception = Assertions.assertThrows(
                DomainException.class,
                () -> genre.validate(new ThrowsValidationHandler())
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }
}