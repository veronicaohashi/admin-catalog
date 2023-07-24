package com.fullcycle.admin.catalog.infraestructure.category.persistence;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.MySQLGatewayTest;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

@MySQLGatewayTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository repository;

    @Test
    void givenAnInvalidNullName_whenCallsSave_thenReturnError() {
        final var expectedPropertyName = "name";
        final var expectedMessage = "not-null property references a null or transient value : com.fullcycle.admin.catalog.infraestructure.category.persistence.CategoryJpaEntity.name";
        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var categoryEntity = CategoryJpaEntity.from(category);
        categoryEntity.setName(null);

        final var error = Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> repository.save(categoryEntity)
        );

        final var cause = Assertions.assertInstanceOf(PropertyValueException.class, error.getCause());
        Assertions.assertEquals(expectedPropertyName, cause.getPropertyName());
        Assertions.assertEquals(expectedMessage, cause.getMessage());
    }

    @Test
    public void givenAnInvalidNullCreatedAt_whenCallsSave_thenReturnError() {
        final var expectedPropertyName = "createdAt";
        final var expectedMessage = "not-null property references a null or transient value : com.fullcycle.admin.catalog.infraestructure.category.persistence.CategoryJpaEntity.createdAt";
        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var categoryEntity = CategoryJpaEntity.from(category);
        categoryEntity.setCreatedAt(null);

        final var error = Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> repository.save(categoryEntity)
        );

        final var cause = Assertions.assertInstanceOf(PropertyValueException.class, error.getCause());
        Assertions.assertEquals(expectedPropertyName, cause.getPropertyName());
        Assertions.assertEquals(expectedMessage, cause.getMessage());
    }

    @Test
    public void givenAnInvalidNullUpdatedAt_whenCallsSave_thenReturnError() {
        final var expectedPropertyName = "updatedAt";
        final var expectedMessage = "not-null property references a null or transient value : com.fullcycle.admin.catalog.infraestructure.category.persistence.CategoryJpaEntity.updatedAt";
        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var categoryEntity = CategoryJpaEntity.from(category);
        categoryEntity.setUpdatedAt(null);

        final var error = Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> repository.save(categoryEntity)
        );

        final var cause = Assertions.assertInstanceOf(PropertyValueException.class, error.getCause());
        Assertions.assertEquals(expectedPropertyName, cause.getPropertyName());
        Assertions.assertEquals(expectedMessage, cause.getMessage());
    }
}
