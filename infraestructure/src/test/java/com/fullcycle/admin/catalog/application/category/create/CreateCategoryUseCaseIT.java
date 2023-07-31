package com.fullcycle.admin.catalog.application.category.create;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.application.category.create.CreateCategoryCommand;
import com.fullcycle.admin.catalog.domain.application.category.create.CreateCategoryUseCase;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.infraestructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@IntegrationTest
public class CreateCategoryUseCaseIT {

    @Autowired
    private CreateCategoryUseCase useCase;

    @Autowired
    private CategoryRepository repository;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    public void givenAValidCommand_whenCallsCreateCategory_thenReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        final var response = useCase.execute(command).get();

        Assertions.assertEquals(1, repository.count());

        final var createdCategory = repository.findById(response.id()).get();
        Assertions.assertEquals(expectedName, createdCategory.getName());
        Assertions.assertEquals(expectedDescription, createdCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, createdCategory.isActive());
        Assertions.assertNotNull(createdCategory.getCreatedAt());
        Assertions.assertNotNull(createdCategory.getUpdatedAt());
        Assertions.assertNull(createdCategory.getDeletedAt());
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
    public void givenAValidCommand_whenGatewayThrowsRandomException_thenReturnAException() {
        final var command = CreateCategoryCommand.with("Name", "Description", true);
        final var expectedErrorMessage = "Gateway error";

        doThrow(new IllegalStateException(expectedErrorMessage))
                .when(categoryGateway).create(any());

        final var response = useCase.execute(command).getLeft();

        Assertions.assertEquals(expectedErrorMessage, response.firstError().message());
        verify(categoryGateway, times(1)).create(any());
    }
}
