package com.fullcycle.admin.catalog.domain.application.category.create;

import com.fullcycle.admin.catalog.domain.application.UseCase;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import io.vavr.control.Either;

public abstract class CreateCategoryUseCase extends UseCase<CreateCategoryCommand, Either<Notification, CreateCategoryOutput>> {

}