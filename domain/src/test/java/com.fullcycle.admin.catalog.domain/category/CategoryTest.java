package com.fullcycle.admin.catalog.domain.category;

import com.fullcycle.admin.catalog.domain.exception.DomainException;
import com.fullcycle.admin.catalog.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    void givenAValidParams_whenCallNewCategory_thenInstantiateACategory() {
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
    void givenAnInvalidNullName_whenCallNewCategory_thenReturnAnException() {
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
    void givenAnInvalidNameLengthGreaterThan255_whenCallNewCategory_thenReturnAnException() {
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
    void givenAnInvalidEmptyName_whenCallNewCategory_thenReturnAnException() {
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
    void givenAValidDescriptionEmpty_whenCallNewCategory_thenInstantiateACategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        Assertions.assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));

        Assertions.assertNotNull(category);
        Assertions.assertEquals(expectedDescription, category.getDescription());
    }

    @Test
    void givenAValidInactive_whenCallNewCategory_thenInstantiateACategory() {
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
    void givenAValidActiveCategory_whenCallDeactivate_thenReturnCategoryInactivated() {
        final var expectedIsActive = false;

        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var updatedAt = category.getUpdatedAt();

        category.deactivate();

        Assertions.assertEquals(expectedIsActive, category.isActive());
        Assertions.assertTrue(category.getUpdatedAt().isAfter(updatedAt));
        Assertions.assertNotNull(category.getDeletedAt());
    }

    @Test
    void givenAValidInactiveCategory_whenCallActivate_thenReturnCategoryActivated() {
        final var expectedIsActive = true;
        final var category = Category.newCategory("Filmes", "A categoria mais assistida", false);
        final var updatedAt = category.getUpdatedAt();

        category.activate();

        Assertions.assertEquals(expectedIsActive, category.isActive());
        Assertions.assertTrue(category.getUpdatedAt().isAfter(updatedAt));
        Assertions.assertNull(category.getDeletedAt());
    }

    @Test
    void givenAValidCategory_whenCallUpdate_thenReturnCategoryUpdated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var category = Category.newCategory("Filmes Antigos", "Categoria menos assistida", false);
        final var expectedCreatedAt = category.getCreatedAt();
        final var expectedUpdatedAt = category.getUpdatedAt();

        category.update(expectedName, expectedDescription, expectedIsActive);

        Assertions.assertEquals(expectedName, category.getName());
        Assertions.assertEquals(expectedDescription, category.getDescription());
        Assertions.assertEquals(expectedIsActive, category.isActive());
        Assertions.assertEquals(expectedCreatedAt, category.getCreatedAt());
        Assertions.assertTrue(category.getUpdatedAt().isAfter(expectedUpdatedAt));
        Assertions.assertNull(category.getDeletedAt());
    }

    @Test
    void givenAValidCategory_whenCallUpdateToInactive_thenReturnCategoryUpdated() {
        final var expectedIsActive = false;
        final var category = Category.newCategory("Filmes", "Categoria mais assistida", true);
        final var expectedCreatedAt = category.getCreatedAt();
        final var expectedUpdatedAt = category.getUpdatedAt();

        category.update(category.getName(), category.getDescription(), expectedIsActive);

        Assertions.assertEquals(expectedIsActive, category.isActive());
        Assertions.assertEquals(expectedCreatedAt, category.getCreatedAt());
        Assertions.assertTrue(category.getUpdatedAt().isAfter(expectedUpdatedAt));
        Assertions.assertNotNull(category.getDeletedAt());
    }

    @Test
    void givenAValidCategory_whenCallUpdateWithInvalidParams_thenReturnCategoryUpdated() {
        final String expectedName = null;
        final var category = Category.newCategory("Filmes", "Categoria mais assistida", true);

        category.update(expectedName, category.getDescription(), true);

        Assertions.assertEquals(expectedName, category.getName());
    }
}
