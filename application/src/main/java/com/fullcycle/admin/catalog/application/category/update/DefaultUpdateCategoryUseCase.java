package com.fullcycle.admin.catalog.application.category.update;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import io.vavr.control.Either;

import java.util.Objects;
import java.util.function.Supplier;

import static io.vavr.API.Left;
import static io.vavr.API.Try;

public class DefaultUpdateCategoryUseCase extends UpdateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultUpdateCategoryUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<Notification, UpdateCategoryOutput> execute(UpdateCategoryCommand updateCategoryCommand) {
        final var id = CategoryID.from(updateCategoryCommand.id());
        final var category = this.categoryGateway.findById(id)
                .orElseThrow(notFound(id));
        final var notification = Notification.create();
        category
                .update(updateCategoryCommand.name(), updateCategoryCommand.description(), updateCategoryCommand.active())
                .validate(notification);

        return notification.hasError() ? Left(notification) : update(category);
    }

    private static Supplier<NotFoundException> notFound(CategoryID id) {
        return () -> NotFoundException.with(Category.class, id);
    }

    private Either<Notification, UpdateCategoryOutput> update(final Category category) {
        return Try(() -> this.categoryGateway.update(category))
                .toEither()
                .bimap(Notification::create, UpdateCategoryOutput::from);
    }
}
