package com.fullcycle.admin.catalog.infraestructure.api.controllers;

import com.fullcycle.admin.catalog.domain.application.category.create.CreateCategoryCommand;
import com.fullcycle.admin.catalog.domain.application.category.create.CreateCategoryOutput;
import com.fullcycle.admin.catalog.domain.application.category.create.CreateCategoryUseCase;
import com.fullcycle.admin.catalog.domain.application.category.delete.DeleteCategoryUseCase;
import com.fullcycle.admin.catalog.domain.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.fullcycle.admin.catalog.domain.application.category.update.UpdateCategoryCommand;
import com.fullcycle.admin.catalog.domain.application.category.update.UpdateCategoryOutput;
import com.fullcycle.admin.catalog.domain.application.category.update.UpdateCategoryUseCase;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import com.fullcycle.admin.catalog.infraestructure.api.CategoryAPI;
import com.fullcycle.admin.catalog.infraestructure.category.models.CategoryApiOutput;
import com.fullcycle.admin.catalog.infraestructure.category.models.CreateCategoryApiInput;
import com.fullcycle.admin.catalog.infraestructure.category.models.UpdateCategoryApiInput;
import com.fullcycle.admin.catalog.infraestructure.category.presenters.CategoryApiPresenter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;
import java.util.function.Function;

@RestController
public class CategoryController implements CategoryAPI {
    private final CreateCategoryUseCase createCategoryUseCase;

    private final GetCategoryByIdUseCase getCategoryByIdUseCase;

    private final UpdateCategoryUseCase updateCategoryUseCase;

    private final DeleteCategoryUseCase deleteCategoryUseCase;

    public CategoryController(
            final CreateCategoryUseCase createCategoryUseCase,
            final GetCategoryByIdUseCase getCategoryByIdUseCase,
            final UpdateCategoryUseCase updateCategoryUseCase,
            final DeleteCategoryUseCase deleteCategoryUseCase
    ) {
        this.createCategoryUseCase = Objects.requireNonNull(createCategoryUseCase);
        this.getCategoryByIdUseCase = Objects.requireNonNull(getCategoryByIdUseCase);
        this.updateCategoryUseCase = Objects.requireNonNull(updateCategoryUseCase);
        this.deleteCategoryUseCase = Objects.requireNonNull(deleteCategoryUseCase);
    }

    @Override
    public ResponseEntity<?> create(final CreateCategoryApiInput input) {
        final var command = CreateCategoryCommand.with(
                input.name(),
                input.description(),
                input.active()
        );

        final Function<Notification, ResponseEntity<?>> onError = notification ->
                ResponseEntity.unprocessableEntity().body(notification);

        final Function<CreateCategoryOutput, ResponseEntity<?>> onSuccess = output ->
                ResponseEntity.created(URI.create("/categories/" + output.id())).body(output);

        return createCategoryUseCase.execute(command).fold(onError, onSuccess);
    }

    @Override
    public Pagination<?> list(String search, int page, int perPage, String sort, String direction) {
        return null;
    }

    @Override
    public CategoryApiOutput getById(final String id) {
        return CategoryApiPresenter.present(getCategoryByIdUseCase.execute(id));
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateCategoryApiInput input) {
        final var command = UpdateCategoryCommand.with(
                id,
                input.name(),
                input.description(),
                input.active()
        );

        final Function<Notification, ResponseEntity<?>> onError = notification ->
                ResponseEntity.unprocessableEntity().body(notification);

        final Function<UpdateCategoryOutput, ResponseEntity<?>> onSuccess = ResponseEntity::ok;

        return updateCategoryUseCase.execute(command).fold(onError, onSuccess);
    }

    @Override
    public void delete(final String id) {
        deleteCategoryUseCase.execute(id);
    }
}
