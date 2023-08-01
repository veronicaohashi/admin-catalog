package com.fullcycle.admin.catalog.application.category.retrieve.get;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.DomainException;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.infraestructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalog.infraestructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

@IntegrationTest
class GetCategoryByIdUseCaseIT {

    @Autowired
    private GetCategoryByIdUseCase useCase;

    @Autowired
    private CategoryRepository repository;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidId_whenCallsGetCategory_thenReturnCategory() {
        final var expectedCategory = Category.newCategory("Film", "Description", true);
        final var expectedId = expectedCategory.getId();
        repository.saveAndFlush(CategoryJpaEntity.from(expectedCategory));

        final var response = useCase.execute(expectedId.getValue());

        Assertions.assertEquals(expectedCategory.getId(), response.id());
        Assertions.assertEquals(expectedCategory.getName(), response.name());
        Assertions.assertEquals(expectedCategory.getDescription(), response.description());
        Assertions.assertEquals(expectedCategory.isActive(), response.active());
    }

    @Test
    void givenAInvalidId_whenCallsGetCategory_thenReturnNotFound() {
        final var expectedMessage = "Category with ID 123 was not found";
        final var invalidId = CategoryID.from("123");

        final var response = Assertions.assertThrows(
                NotFoundException.class,
                () -> useCase.execute(invalidId.getValue())
        );

        Assertions.assertEquals(expectedMessage, response.getMessage());
    }

    @Test
    void givenAValidId_whenGatewayThrowsException_thenReturnException() {
        final var expectedMessage = "Gateway error";
        final var invalidId = CategoryID.from("123");
        doThrow(new IllegalStateException(expectedMessage)).when(categoryGateway).findById(eq(invalidId));

        final var response = Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(invalidId.getValue()));

        Assertions.assertEquals(expectedMessage, response.getMessage());

    }
}
