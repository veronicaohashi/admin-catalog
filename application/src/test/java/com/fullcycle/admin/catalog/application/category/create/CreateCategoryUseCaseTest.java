package com.fullcycle.admin.catalog.application.category.create;

import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateCategoryUseCaseTest {

    @InjectMocks
    private DefaultCreateCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsCreateCategory_thenReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);
        when(categoryGateway.create(any())).then(returnsFirstArg());

        final var response = useCase.execute(command).get();

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.id());
        verify(categoryGateway, times(1)).create(argThat(category ->
                Objects.equals(expectedName, category.getName())
                        && Objects.equals(expectedDescription, category.getDescription())
                        && Objects.equals(expectedIsActive, category.isActive())
                        && Objects.nonNull(category.getId())
                        && Objects.nonNull(category.getCreatedAt())
                        && Objects.nonNull(category.getUpdatedAt())
                        && Objects.isNull(category.getDeletedAt())
        ));
    }

    @Test
    public void givenAnInvalidName_whenCallsCreateCategory_thenReturnDomainException() {
        final String invalidName = null;
        final var command = CreateCategoryCommand.with(invalidName, "Description", true);
        final var expectedErrorMessage = "'name' should not be null";

        final var notification = useCase.execute(command).getLeft();

        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());
        verify(categoryGateway, times(0)).create(any());
    }

    @Test
    public void givenAValidInactivateCommand_whenCallsCreateCategory_shouldReturnInactiveCategoryId() {
        final boolean expectedActive = false;
        final var command = CreateCategoryCommand.with("name", "Description", expectedActive);

        when(categoryGateway.create(any())).then(returnsFirstArg());

        final var response = useCase.execute(command).get();

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.id());
        verify(categoryGateway, times(1)).create(argThat(category ->
                Objects.equals(expectedActive, category.isActive())
                        && Objects.nonNull(category.getCreatedAt())
                        && Objects.nonNull(category.getUpdatedAt())
                        && Objects.nonNull(category.getDeletedAt())
        ));
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsRandomException_thenReturnAException() {
        final var command = CreateCategoryCommand.with("Name", "Description", true);
        final var expectedErrorMessage = "Gateway error";

        when(categoryGateway.create(any())).thenThrow(new IllegalStateException(expectedErrorMessage));

        final var response = useCase.execute(command).getLeft();

        Assertions.assertEquals(expectedErrorMessage, response.firstError().message());
        verify(categoryGateway, times(1)).create(any());
    }
}
