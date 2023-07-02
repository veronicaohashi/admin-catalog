package com.fullcycle.admin.catalog.domain.application.category.update;

import com.fullcycle.admin.catalog.domain.application.UseCase;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import io.vavr.control.Either;

public abstract class UpdateCategoryUseCase extends UseCase<UpdateCategoryCommand, Either<Notification, UpdateCategoryOutput>> {
}
