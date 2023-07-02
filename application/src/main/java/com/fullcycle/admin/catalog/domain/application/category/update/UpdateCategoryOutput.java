package com.fullcycle.admin.catalog.domain.application.category.update;

import com.fullcycle.admin.catalog.domain.application.category.create.CreateCategoryOutput;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryID;

public record UpdateCategoryOutput(
        CategoryID id
) {
    public static UpdateCategoryOutput from(final Category category) {
        return new UpdateCategoryOutput(category.getId());
    }
}
