package com.fullcycle.admin.catalog.domain.application.category.update;

import com.fullcycle.admin.catalog.domain.application.category.create.CreateCategoryCommand;

public record UpdateCategoryCommand(
        String id,
        String name,
        String description,
        boolean active
) {
    public static UpdateCategoryCommand with(
            final String id,
            final String name,
            final String description,
            final boolean active
    ) {
        return new UpdateCategoryCommand(id, name, description, active);
    }
}
