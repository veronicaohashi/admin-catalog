package com.fullcycle.admin.catalog.application.category.update;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.application.category.update.UpdateCategoryCommand;
import com.fullcycle.admin.catalog.domain.application.category.update.UpdateCategoryUseCase;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.exception.DomainException;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.infraestructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalog.infraestructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@IntegrationTest
class UpdateCategoryUseCaseIT {
    @Autowired
    private UpdateCategoryUseCase useCase;

    @Autowired
    private CategoryRepository repository;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() {
        final var category = Category.newCategory("", null, true);
        repository.saveAndFlush(CategoryJpaEntity.from(category));

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = category.getId();
        final var command = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        final var response = useCase.execute(command).get();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedId.getValue(), response.id());

        final var categoryFromDB = repository.findById(expectedId.getValue()).get();
        Assertions.assertEquals(expectedName, categoryFromDB.getName());
        Assertions.assertEquals(expectedDescription, categoryFromDB.getDescription());
        Assertions.assertEquals(expectedIsActive, categoryFromDB.isActive());
        Assertions.assertTrue(category.getUpdatedAt().isBefore(categoryFromDB.getUpdatedAt()));
        Assertions.assertNull(categoryFromDB.getDeletedAt());
    }

    @Test
    void givenAnInvalidName_whenCallsUpdateCategory_thenReturnDomainException() {
        final var category = Category.newCategory("Film", "Description", true);
        repository.saveAndFlush(CategoryJpaEntity.from(category));

        final String invalidName = null;
        final var expectedId = category.getId();
        final var command = UpdateCategoryCommand.with(expectedId.getValue(), invalidName, "Description", true);
        final var expectedErrorMessage = "'name' should not be null";

        final var notification = useCase.execute(command).getLeft();

        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());
        verify(categoryGateway, times(0)).update(any());
    }

    @Test
    void givenAValidInactivateCommand_whenCallsUpdateCategory_shouldReturnInactiveCategoryId() {
        final var category = Category.newCategory("Film", "Description", true);
        repository.saveAndFlush(CategoryJpaEntity.from(category));

        final boolean expectedActive = false;
        final var expectedId = category.getId();
        final var command = UpdateCategoryCommand.with(expectedId.getValue(), "Film", "Description", expectedActive);

        useCase.execute(command).get();

        final var categoryFromDB = repository.findById(expectedId.getValue()).get();
        Assertions.assertEquals(expectedActive, categoryFromDB.isActive());
        Assertions.assertNotNull(categoryFromDB.getDeletedAt());
    }

    @Test
    void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnAException() {
        final var category = Category.newCategory("Film", "Description", true);
        repository.saveAndFlush(CategoryJpaEntity.from(category));

        final var command = UpdateCategoryCommand.with(category.getId().getValue(), "Film", "Description", true);
        final var expectedErrorMessage = "Gateway error";
        doThrow(new IllegalStateException(expectedErrorMessage))
                .when(categoryGateway).update(any());

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

        final var notification = Assertions.assertThrows(NotFoundException.class, () -> useCase.execute(command));

        Assertions.assertEquals(expectedErrorMessage, notification.getMessage());
        verify(categoryGateway, times(1)).findById(any());
        verify(categoryGateway, times(0)).update(any());
    }
}
