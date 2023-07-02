package com.fullcycle.admin.catalog.domain.application.category.update;

import com.fullcycle.admin.catalog.domain.application.category.create.CreateCategoryCommand;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateCategoryUseCaseTest {

    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Test
    public void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var createdCategory = Category.newCategory("", null, true);
        final var expectedId = createdCategory.getId();
        final var command = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(createdCategory));

        when(categoryGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var response = useCase.execute(command).get();

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.id());
        verify(categoryGateway, times(1)).update(argThat(category ->
                Objects.equals(expectedName, category.getName())
                        && Objects.equals(expectedDescription, category.getDescription())
                        && Objects.equals(expectedIsActive, category.isActive())
                        && Objects.equals(expectedId, category.getId())
                        && Objects.equals(createdCategory.getCreatedAt(), category.getCreatedAt())
                        && Objects.equals(createdCategory.getUpdatedAt(), category.getUpdatedAt()) // TODO: Fix assertion
                        && Objects.isNull(category.getDeletedAt())
        ));
    }
}
