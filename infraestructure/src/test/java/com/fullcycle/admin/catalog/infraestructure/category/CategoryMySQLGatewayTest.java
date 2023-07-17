package com.fullcycle.admin.catalog.infraestructure.category;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.infraestructure.MySQLGatewayTest;
import com.fullcycle.admin.catalog.infraestructure.category.persistance.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@MySQLGatewayTest
public class CategoryMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAValidCategory_whenCallsCreate_thenReturnANewCategory() {
        final var expectedName = "Filme";
        final var expectedDescription = "Um filme";
        final var expectedIsActive = true;
        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        Assertions.assertEquals(0, categoryRepository.count());

        final var response = categoryGateway.create(category);

        Assertions.assertEquals(1, categoryRepository.count());
        Assertions.assertEquals(category.getId(), response.getId());
        Assertions.assertEquals(expectedName, response.getName());
        Assertions.assertEquals(expectedDescription, response.getDescription());
        Assertions.assertEquals(expectedIsActive, response.isActive());
        Assertions.assertEquals(category.getCreatedAt(), response.getCreatedAt());
        Assertions.assertEquals(category.getUpdatedAt(), response.getUpdatedAt());
        Assertions.assertEquals(category.getDeletedAt(), response.getDeletedAt());

        final var persistedEntity = categoryRepository.findById(category.getId().getValue()).get();
        Assertions.assertEquals(category.getId().getValue(), persistedEntity.getId());
        Assertions.assertEquals(expectedName, persistedEntity.getName());
        Assertions.assertEquals(expectedDescription, persistedEntity.getDescription());
        Assertions.assertEquals(expectedIsActive, persistedEntity.isActive());
        Assertions.assertEquals(category.getCreatedAt(), persistedEntity.getCreatedAt());
        Assertions.assertEquals(category.getUpdatedAt(), persistedEntity.getUpdatedAt());
        Assertions.assertEquals(category.getDeletedAt(), persistedEntity.getDeletedAt());


    }
}
