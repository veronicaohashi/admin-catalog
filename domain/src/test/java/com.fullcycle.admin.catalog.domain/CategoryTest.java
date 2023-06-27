package com.fullcycle.admin.catalog.domain;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.exception.DomainException;
import com.fullcycle.admin.catalog.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CategoryTest {

    @Test
    public void givenAValidParams_whenCallNewCategory_thenInstantiateACategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        Assertions.assertNotNull(category);
        Assertions.assertNotNull(category.getId());
        Assertions.assertEquals(expectedName, category.getName());
        Assertions.assertEquals(expectedDescription, category.getDescription());
        Assertions.assertEquals(expectedIsActive, category.isActive());
        Assertions.assertNotNull(category.getCreatedAt());
        Assertions.assertNotNull(category.getUpdatedAt());
        Assertions.assertNull(category.getDeletedAt());
    }

    @Test
    public void givenAnInvalidNullName_whenCallNewCategory_thenReturnAnException() {
        final String invalidName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedIsActive = true;

        final var category = Category.newCategory(invalidName, expectedDescription, expectedIsActive);

        final var exception = Assertions.assertThrows(DomainException.class, () -> category.validate(new ThrowsValidationHandler()));

        Assertions.assertEquals(1, exception.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNameLengthLessThan3_whenCallNewCategory_thenReturnAnException() {
        final var invalidName = """
                A certificação de metodologias que nos auxiliam a lidar com a crescente influência da mídia talvez venha
                 a ressaltar a relatividade do processo de comunicação como um todo. Todas estas questões, devidamente 
                 ponderadas, levantam dúvidas sobre se o surgimento do comércio virtual promove a alavancagem do sistema 
                 de participação geral.                                                                                     
                """;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedErrorMessage = "'name' must be between 3 and 255 character";
        final var expectedIsActive = true;

        final var category = Category.newCategory(invalidName, expectedDescription, expectedIsActive);

        final var exception = Assertions.assertThrows(DomainException.class, () -> category.validate(new ThrowsValidationHandler()));

        Assertions.assertEquals(1, exception.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyName_whenCallNewCategory_thenReturnAnException() {
        final var invalidName = " ";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedIsActive = true;

        final var category = Category.newCategory(invalidName, expectedDescription, expectedIsActive);

        final var exception = Assertions.assertThrows(DomainException.class, () -> category.validate(new ThrowsValidationHandler()));

        Assertions.assertEquals(1, exception.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenAValidDescriptionEmpty_whenCallNewCategory_thenInstantiateACategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        Assertions.assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));

        Assertions.assertNotNull(category);
        Assertions.assertEquals(expectedDescription, category.getDescription());
    }


    @Test
    public void givenAValidInactive_whenCallNewCategory_thenInstantiateACategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var category =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        Assertions.assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));

        Assertions.assertNotNull(category);
        Assertions.assertEquals(expectedIsActive, category.isActive());
        Assertions.assertNotNull(category.getDeletedAt());
    }

    @Test
    public void givenAValidActiveCategory_whenCallDeactivate_thenReturnCategoryInactivated() {
        final var expectedIsActive = false;

        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var updatedAt = category.getUpdatedAt();

        category.deactivate();

        Assertions.assertEquals(expectedIsActive, category.isActive());
        Assertions.assertTrue(category.getUpdatedAt().isAfter(updatedAt));
        Assertions.assertNotNull(category.getDeletedAt());
    }

    @Test
    public void givenAValidInactiveCategory_whenCallActivate_thenReturnCategoryActivated() {
        final var expectedIsActive = true;

        final var category = Category.newCategory("Filmes", "A categoria mais assistida", false);
        final var updatedAt = category.getUpdatedAt();

        category.activate();

        Assertions.assertEquals(expectedIsActive, category.isActive());
        Assertions.assertTrue(category.getUpdatedAt().isAfter(updatedAt));
        Assertions.assertNull(category.getDeletedAt());
    }
}
