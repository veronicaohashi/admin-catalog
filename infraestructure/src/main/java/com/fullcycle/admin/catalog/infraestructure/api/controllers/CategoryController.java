package com.fullcycle.admin.catalog.infraestructure.api.controllers;

import com.fullcycle.admin.catalog.application.category.create.CreateCategoryCommand;
import com.fullcycle.admin.catalog.application.category.create.CreateCategoryOutput;
import com.fullcycle.admin.catalog.application.category.create.CreateCategoryUseCase;
import com.fullcycle.admin.catalog.application.category.delete.DeleteCategoryUseCase;
import com.fullcycle.admin.catalog.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.fullcycle.admin.catalog.application.category.retrieve.list.ListCategoriesUseCase;
import com.fullcycle.admin.catalog.application.category.update.UpdateCategoryCommand;
import com.fullcycle.admin.catalog.application.category.update.UpdateCategoryOutput;
import com.fullcycle.admin.catalog.application.category.update.UpdateCategoryUseCase;
import com.fullcycle.admin.catalog.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import com.fullcycle.admin.catalog.infraestructure.api.CategoryAPI;
import com.fullcycle.admin.catalog.infraestructure.category.models.CategoryResponse;
import com.fullcycle.admin.catalog.infraestructure.category.models.CategoryListResponse;
import com.fullcycle.admin.catalog.infraestructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalog.infraestructure.category.models.UpdateCategoryRequest;
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

    private final ListCategoriesUseCase listCategoriesUseCase;

    public CategoryController(
            final CreateCategoryUseCase createCategoryUseCase,
            final GetCategoryByIdUseCase getCategoryByIdUseCase,
            final UpdateCategoryUseCase updateCategoryUseCase,
            final DeleteCategoryUseCase deleteCategoryUseCase,
            final ListCategoriesUseCase listCategoriesUseCase
    ) {
        this.createCategoryUseCase = Objects.requireNonNull(createCategoryUseCase);
        this.getCategoryByIdUseCase = Objects.requireNonNull(getCategoryByIdUseCase);
        this.updateCategoryUseCase = Objects.requireNonNull(updateCategoryUseCase);
        this.deleteCategoryUseCase = Objects.requireNonNull(deleteCategoryUseCase);
        this.listCategoriesUseCase = Objects.requireNonNull(listCategoriesUseCase);
    }

    @Override
    public ResponseEntity<?> create(final CreateCategoryRequest input) {
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
    public Pagination<CategoryListResponse> list(
            final String search,
            final int page,
            final int perPage,
            final String sort,
            final String direction
    ) {
        return listCategoriesUseCase.execute(
                new CategorySearchQuery(
                        page, perPage, search, sort, direction
                )
        ).map(CategoryApiPresenter::present);
    }

    @Override
    public CategoryResponse getById(final String id) {
        return CategoryApiPresenter.present(getCategoryByIdUseCase.execute(id));
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateCategoryRequest input) {
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
