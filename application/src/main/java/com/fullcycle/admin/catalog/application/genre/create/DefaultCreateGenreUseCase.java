package com.fullcycle.admin.catalog.application.genre.create;

import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultCreateGenreUseCase extends CreateGenreUseCase{

    private final GenreGateway genreGateway;

    private final CategoryGateway categoryGateway;

    public DefaultCreateGenreUseCase(final GenreGateway genreGateway, final CategoryGateway categoryGateway) {
        this.genreGateway = Objects.requireNonNull(genreGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public CreateGenreOutput execute(final CreateGenreCommand createGenreCommand) {
        final var name = createGenreCommand.name();
        final var isActive = createGenreCommand.isActive();
        final var categories = toCategoryID(createGenreCommand.categories());

        final var notification = Notification.create();
        notification.append(validateCategories(categories));

        final var genre = notification.validate(() -> Genre.newGenre(name, isActive));

        if(notification.hasError()) {
            throw new NotificationException("Could not create Aggregate Genre", notification);
        }

        return CreateGenreOutput.from(this.genreGateway.create(genre));
    }

    private ValidationHandler validateCategories(final List<CategoryID> ids) {
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

    private List<CategoryID> toCategoryID(final List<String> ids) {
        return ids.stream()
                .map(CategoryID::from)
                .toList();

    }
}
