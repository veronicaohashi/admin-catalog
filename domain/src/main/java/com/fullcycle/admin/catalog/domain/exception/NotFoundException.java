package com.fullcycle.admin.catalog.domain.exception;

import com.fullcycle.admin.catalog.domain.AggregateRoot;
import com.fullcycle.admin.catalog.domain.Identifier;
import com.fullcycle.admin.catalog.domain.validation.Error;

import java.util.Collections;
import java.util.List;

public class NotFoundException extends DomainException {
    protected NotFoundException(final String message, final List<Error> errors) {
        super(message, errors);
    }

    public static NotFoundException with(
            final Class<? extends AggregateRoot<?>> className,
            final Identifier id
    ) {
        final var error = "%s with ID %s was not found".formatted(
                className.getSimpleName(),
                id.getValue()
        );

        return new NotFoundException(error, Collections.emptyList());
    }
}
