package com.fullcycle.admin.catalog.application.category.create;

import com.fullcycle.admin.catalog.domain.category.Category;

public record CreateCategoryOutput(
        String id
) {

    public static CreateCategoryOutput from(final String id) {
        return new CreateCategoryOutput(id);
    }

    public static CreateCategoryOutput from(final Category category) {
        return new CreateCategoryOutput(category.getId().getValue());
    }
}
