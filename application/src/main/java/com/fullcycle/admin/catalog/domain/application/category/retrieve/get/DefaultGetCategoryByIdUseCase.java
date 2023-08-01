package com.fullcycle.admin.catalog.domain.application.category.retrieve.get;

import com.fullcycle.admin.catalog.domain.application.UnitUseCase;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.DomainException;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.validation.Error;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultGetCategoryByIdUseCase extends GetCategoryByIdUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultGetCategoryByIdUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }


    @Override
    public CategoryOutput execute(final String id) {
        final var categoryId = CategoryID.from(id);
        return categoryGateway.findById(categoryId)
                .map(CategoryOutput::from)
                .orElseThrow(notFound(categoryId));
    }

    private Supplier<NotFoundException> notFound(final CategoryID id) {
        return () -> NotFoundException.with(Category.class, id);
    }
}
