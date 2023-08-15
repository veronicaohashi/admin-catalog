package com.fullcycle.admin.catalog.domain.genre;

import com.fullcycle.admin.catalog.domain.AggregateRoot;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import com.fullcycle.admin.catalog.domain.utils.InstantUtils;
import com.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Genre extends AggregateRoot<GenreID> {

    private String name;
    private boolean active;
    private List<CategoryID> categories;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    protected Genre(
            final GenreID genreID,
            final String name,
            final boolean active,
            final List<CategoryID> categories,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        super(genreID);
        this.name = name;
        this.active = active;
        this.categories = categories;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        selfValidate();
    }

    private void selfValidate() {
        final var notification = Notification.create();
        validate(notification);

        if(notification.hasError()) {
            throw new NotificationException("Failed to create a Aggregate Genre", notification);
        }
    }

    public static Genre newGenre(
            final String name,
            final boolean active
    ) {
        final var id = GenreID.unique();
        final var now = InstantUtils.now();
        final var deletedAt = active ? null : now;
        return new Genre(id, name, active, new ArrayList<>(), now, now, deletedAt);
    }

    public static Genre with(
            final GenreID id,
            final String name,
            final boolean active,
            final List<CategoryID> categories,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        return new Genre(
                id,
                name,
                active,
                categories,
                createdAt,
                updatedAt,
                deletedAt
        );
    }

    public static Genre with(final Genre genre) {
        return new Genre(
                genre.id,
                genre.name,
                genre.active,
                new ArrayList<>(genre.categories),
                genre.createdAt,
                genre.updatedAt,
                genre.deletedAt
        );
    }

    public Genre deactivate() {
        if(getDeletedAt() == null) {
            this.deletedAt = InstantUtils.now();
        }
        active = false;
        updatedAt = InstantUtils.now();
        return this;
    }

    public Genre activate() {
        active = true;
        updatedAt = InstantUtils.now();
        deletedAt = null;
        return this;
    }

    public Genre update(
            final String name,
            final boolean active,
            final List<CategoryID> categories
    ) {
        if(active) {
            activate();
        } else {
            deactivate();
        }
        this.name = name;
        this.categories = new ArrayList<>(categories);
        this.updatedAt = InstantUtils.now();
        selfValidate();
        return this;
    }

    public Genre addCategory(final CategoryID categoryID) {
        if(categoryID == null) {
            return this;
        }
        categories.add(categoryID);
        updatedAt = InstantUtils.now();
        return this;
    }

    public Genre addCategories(final List<CategoryID> categoryIDS) {
        if(categoryIDS == null || categoryIDS.isEmpty()) {
            return this;
        }
        categories.addAll(categoryIDS);
        updatedAt = InstantUtils.now();
        return this;
    }

    public Genre removeCategory(final CategoryID categoryID) {
        if(categoryID == null) {
            return this;
        }
        categories.remove(categoryID);
        updatedAt = InstantUtils.now();
        return this;
    }

    @Override
    public void validate(ValidationHandler handler) {
        new GenreValidator(this, handler).validate();
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public List<CategoryID> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }
}
