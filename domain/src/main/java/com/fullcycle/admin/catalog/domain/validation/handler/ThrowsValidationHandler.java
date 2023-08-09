package com.fullcycle.admin.catalog.domain.validation.handler;

import com.fullcycle.admin.catalog.domain.exception.DomainException;
import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.validation.ValidationHandler;

import java.util.List;

public class ThrowsValidationHandler implements ValidationHandler {
    @Override
    public ValidationHandler append(Error error) {
        throw DomainException.with(error);
    }

    @Override
    public ValidationHandler append(ValidationHandler validationHandler) {
        throw DomainException.with(validationHandler.getErrors());
    }

    @Override
    public <T> T validate(Validation<T> validation) {
        try {
            return validation.validate();
        } catch (final Exception ex) {
            throw DomainException.with(new Error(ex.getMessage()));
        }
    }

    @Override
    public List<Error> getErrors() {
        return List.of();
    }
}
