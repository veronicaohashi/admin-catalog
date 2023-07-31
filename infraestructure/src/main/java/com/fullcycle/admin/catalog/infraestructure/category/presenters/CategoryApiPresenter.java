package com.fullcycle.admin.catalog.infraestructure.category.presenters;

import com.fullcycle.admin.catalog.domain.application.category.retrieve.get.CategoryOutput;
import com.fullcycle.admin.catalog.infraestructure.category.models.CategoryApiOutput;

public interface CategoryApiPresenter {

    static CategoryApiOutput present(final CategoryOutput output) {
        return new CategoryApiOutput(
                output.id().getValue(),
                output.name(),
                output.description(),
                output.active(),
                output.createdAt(),
                output.updatedAt(),
                output.deletedAt()
        );
    }
}
