package com.fullcycle.admin.catalog.infraestructure.category.presenters;

import com.fullcycle.admin.catalog.domain.application.category.retrieve.get.CategoryOutput;
import com.fullcycle.admin.catalog.domain.application.category.retrieve.list.CategoryListOutput;
import com.fullcycle.admin.catalog.infraestructure.category.models.CategoryResponse;
import com.fullcycle.admin.catalog.infraestructure.category.models.CategoryListResponse;

public interface CategoryApiPresenter {

    static CategoryResponse present(final CategoryOutput output) {
        return new CategoryResponse(
                output.id().getValue(),
                output.name(),
                output.description(),
                output.active(),
                output.createdAt(),
                output.updatedAt(),
                output.deletedAt()
        );
    }
    static CategoryListResponse present(final CategoryListOutput output) {
        return new CategoryListResponse(
                output.id().getValue(),
                output.name(),
                output.description(),
                output.active(),
                output.createdAt(),
                output.deletedAt()
        );
    }
}
