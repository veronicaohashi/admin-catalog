package com.fullcycle.admin.catalog.application.genre.update;

import com.fullcycle.admin.catalog.domain.Identifier;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DefaultUpdateGenreUseCase extends UpdateGenreUseCase {

    private final CategoryGateway categoryGateway;

    private final GenreGateway genreGateway;

    public DefaultUpdateGenreUseCase(final CategoryGateway categoryGateway, final GenreGateway genreGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public UpdateGenreOutput execute(UpdateGenreCommand updateGenreCommand) {
        final var id = GenreID.from(updateGenreCommand.id());
        final var name = updateGenreCommand.name();
        final var isActive = updateGenreCommand.isActive();
        final var categoryIDS = toCategoryID(updateGenreCommand.categories());

        final var genre = genreGateway.findById(id).orElseThrow(notFound(id));

        final var notification = Notification.create();
        notification.append(validateCategories(categoryIDS));
        notification.validate(() -> genre.update(name, isActive, categoryIDS));

        if(notification.hasError()) {
            throw new NotificationException(
                    "Could not update Aggregate Genre %s".formatted(updateGenreCommand.id()), notification
            );
        }

        return UpdateGenreOutput.from(genreGateway.update(genre));
    }

    private ValidationHandler validateCategories(List<CategoryID> ids) {
        final var notification = Notification.create();
        if(ids == null || ids.isEmpty()) {
            return notification;
        }

        final var existingIds = categoryGateway.existsByIds(ids);
        if(ids.size() != existingIds.size()) {
            final var missingIds = new ArrayList<>(ids);
            missingIds.removeAll(existingIds);

            final var errorMessage = missingIds.stream()
                    .map(CategoryID::getValue)
                    .collect(Collectors.joining(", "));

            notification.append(new Error(
                    "Some categories could not be found: %s".formatted(errorMessage)
            ));
        }

        return notification;
    }

    private static Supplier<NotFoundException> notFound(Identifier id) {
        return () -> NotFoundException.with(Genre.class, id);
    }

    private List<CategoryID> toCategoryID(final List<String> ids) {
        return ids.stream()
                .map(CategoryID::from)
                .toList();
    }
}
