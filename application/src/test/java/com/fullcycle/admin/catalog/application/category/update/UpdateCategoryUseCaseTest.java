package com.fullcycle.admin.catalog.application.category.update;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateCategoryUseCaseTest {

    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
    }

    @Test
    void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var createdCategory = Category.newCategory("", null, true);
        final var expectedId = createdCategory.getId();
        final var command = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(Category.with(createdCategory)));

        when(categoryGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var response = useCase.execute(command).get();

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.id());
        verify(categoryGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, times(1)).update(argThat(updatedCategory ->
                Objects.equals(expectedName, updatedCategory.getName())
                        && Objects.equals(expectedDescription, updatedCategory.getDescription())
                        && Objects.equals(expectedIsActive, updatedCategory.isActive())
                        && Objects.equals(expectedId, updatedCategory.getId())
                        && Objects.equals(createdCategory.getCreatedAt(), updatedCategory.getCreatedAt())
                        && createdCategory.getUpdatedAt().isBefore(updatedCategory.getUpdatedAt())
                        && Objects.isNull(updatedCategory.getDeletedAt())
        ));
    }

    @Test
    void givenAnInvalidName_whenCallsUpdateCategory_thenReturnDomainException() {
        final String invalidName = null;
        final var category = Category.newCategory("Film", "Description", true);
        final var expectedId = category.getId();
        final var command = UpdateCategoryCommand.with(expectedId.getValue(), invalidName, "Description", true);
        final var expectedErrorMessage = "'name' should not be null";
        when(categoryGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(Category.with(category)));
        final var notification = useCase.execute(command).getLeft();

        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());
        verify(categoryGateway, times(0)).update(any());
    }

    @Test
    void givenAValidInactivateCommand_whenCallsUpdateCategory_shouldReturnInactiveCategoryId() {
        final boolean expectedActive = false;
        final var category = Category.newCategory("Film", "Description", true);
        final var expectedId = category.getId();
        final var command = UpdateCategoryCommand.with(expectedId.getValue(), "Film", "Description", expectedActive);
        when(categoryGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(Category.with(category)));
        when(categoryGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var response = useCase.execute(command).get();

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.id());
        verify(categoryGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, times(1)).update(argThat(updatedCategory ->
                Objects.equals(expectedId, updatedCategory.getId())
                        && Objects.equals(expectedActive, updatedCategory.isActive())
                        && Objects.equals(category.getCreatedAt(), updatedCategory.getCreatedAt())
                        && category.getUpdatedAt().isBefore(updatedCategory.getUpdatedAt())
                        && Objects.nonNull(updatedCategory.getDeletedAt())
        ));
    }

    @Test
    void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnAException() {
        final var category = Category.newCategory("Film", "Description", true);
        final var command = UpdateCategoryCommand.with(category.getId().getValue(), "Film", "Description", true);
        final var expectedErrorMessage = "Gateway error";
        when(categoryGateway.findById(eq(category.getId())))
                .thenReturn(Optional.of(Category.with(category)));
        when(categoryGateway.update(any()))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var notification = useCase.execute(command).getLeft();

        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());
        verify(categoryGateway, times(1)).findById(any());
        verify(categoryGateway, times(1)).update(any());
    }

    @Test
    void givenACommandWithInvalidID_whenCallsUpdateCategory_shouldReturnNotFoundException() {
        final var expectedId = "123";
        final var command = UpdateCategoryCommand.with(expectedId, "Film", "Description", true);
        final var expectedErrorMessage = "Category with ID 123 was not found";
        when(categoryGateway.findById(eq(CategoryID.from(command.id()))))
                .thenReturn(Optional.empty());

        final var notification = Assertions.assertThrows(NotFoundException.class, () -> useCase.execute(command));

        Assertions.assertEquals(expectedErrorMessage, notification.getMessage());
        verify(categoryGateway, times(1)).findById(any());
        verify(categoryGateway, times(0)).update(any());
    }
}
