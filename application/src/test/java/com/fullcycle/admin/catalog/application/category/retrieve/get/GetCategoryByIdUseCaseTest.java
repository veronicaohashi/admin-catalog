package com.fullcycle.admin.catalog.application.category.retrieve.get;

import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class GetCategoryByIdUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultGetCategoryByIdUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;
    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway);
    }

    @Test
    void givenAValidId_whenCallsGetCategory_thenReturnCategory() {
        final var expectedCategory = Category.newCategory("Film", "Description", true);
        final var expectedId = expectedCategory.getId();
        when(categoryGateway.findById(eq(expectedId))).thenReturn(Optional.of(Category.with(expectedCategory)));

        final var response = useCase.execute(expectedId.getValue());

        Assertions.assertEquals(expectedCategory.getId(), response.id());
        Assertions.assertEquals(expectedCategory.getName(), response.name());
        Assertions.assertEquals(expectedCategory.getDescription(), response.description());
        Assertions.assertEquals(expectedCategory.isActive(), response.active());
        Assertions.assertEquals(expectedCategory.getCreatedAt(), response.createdAt());
        Assertions.assertEquals(expectedCategory.getUpdatedAt(), response.updatedAt());
        Assertions.assertEquals(expectedCategory.getDeletedAt(), response.deletedAt());
    }

    @Test
    void givenAInvalidId_whenCallsGetCategory_thenReturnNotFound() {
        final var expectedMessage = "Category with ID 123 was not found";
        final var invalidId = CategoryID.from("123");
        when(categoryGateway.findById(eq(invalidId))).thenReturn(Optional.empty());

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
        when(categoryGateway.findById(eq(invalidId))).thenThrow(new IllegalStateException(expectedMessage));

        final var response = Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(invalidId.getValue()));

        Assertions.assertEquals(expectedMessage, response.getMessage());

    }
}
