package com.fullcycle.admin.catalog.domain.application.category.retrieve.get;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.DomainException;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCategoryByIdUseCaseTest {

    @InjectMocks
    private DefaultGetCategoryByIdUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
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
