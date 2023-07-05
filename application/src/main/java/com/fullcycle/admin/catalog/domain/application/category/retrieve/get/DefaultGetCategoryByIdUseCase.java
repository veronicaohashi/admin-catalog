package com.fullcycle.admin.catalog.domain.application.category.retrieve.get;

import com.fullcycle.admin.catalog.domain.application.UnitUseCase;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.DomainException;
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
        return categoryGateway.findById(CategoryID.from(id))
                .map(CategoryOutput::from)
                .orElseThrow(notFound(id));
    }

    private Supplier<DomainException> notFound(final String id) {
        return () -> DomainException.with(
            new Error("Category with ID %s was not found".formatted(id))
        );
    }
}
