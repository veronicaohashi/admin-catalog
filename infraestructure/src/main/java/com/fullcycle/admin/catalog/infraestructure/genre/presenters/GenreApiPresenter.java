package com.fullcycle.admin.catalog.infraestructure.genre.presenters;

import com.fullcycle.admin.catalog.application.category.retrieve.get.CategoryOutput;
import com.fullcycle.admin.catalog.application.category.retrieve.list.CategoryListOutput;
import com.fullcycle.admin.catalog.application.genre.retrieve.get.GenreOutput;
import com.fullcycle.admin.catalog.application.genre.retrieve.list.GenreListOutput;
import com.fullcycle.admin.catalog.infraestructure.category.models.CategoryListResponse;
import com.fullcycle.admin.catalog.infraestructure.category.models.CategoryResponse;
import com.fullcycle.admin.catalog.infraestructure.genre.models.GenreListResponse;
import com.fullcycle.admin.catalog.infraestructure.genre.models.GenreResponse;

public interface GenreApiPresenter {
    static GenreResponse present(final GenreOutput output) {
        return new GenreResponse(
                output.id(),
                output.name(),
                output.isActive(),
                output.categories(),
                output.createdAt(),
                output.updatedAt(),
                output.deletedAt()
        );
    }
    static GenreListResponse present(final GenreListOutput output) {
        return new GenreListResponse(
                output.id(),
                output.name(),
                output.isActive(),
                output.createdAt(),
                output.deletedAt()
        );
    }
}
