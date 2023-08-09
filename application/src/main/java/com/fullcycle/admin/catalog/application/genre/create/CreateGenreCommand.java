package com.fullcycle.admin.catalog.application.genre.create;

import java.util.List;

public record CreateGenreCommand(
        String name,
        boolean isActive,
        List<String> categories
) {

    public static CreateGenreCommand with(
            final String name,
            final boolean isActive,
            final List<String> categories
    ) {
        return new CreateGenreCommand(
                name, isActive, categories
        );
    }
}
