package com.fullcycle.admin.catalog.domain.validation.handler;

import com.fullcycle.admin.catalog.domain.exception.DomainException;
import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.List;

public class Notification implements ValidationHandler {

    private final List<Error> errors;

    public Notification(List<Error> errors) {
        this.errors = errors;
    }

    public static Notification create(final Error error) {
        return new Notification(new ArrayList<>()).append(error);
    }

    public static Notification create() {
        return new Notification(new ArrayList<>());
    }

    public static Notification create(Throwable t) {
        return create(new Error(t.getMessage()));
    }

    @Override
    public Notification append(final Error error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public Notification append(final ValidationHandler validationHandler) {
        this.errors.addAll(validationHandler.getErrors());
        return this;
    }

    @Override
    public <T> T validate(final Validation<T> validation) {
        try {
           return validation.validate();
        } catch (final DomainException domainException) {
            this.errors.addAll(domainException.getErrors());
        } catch (final Throwable t) {
            this.errors.add(new Error(t.getMessage()));
        }
        return null;
    }

    @Override
    public List<Error> getErrors() {
        return this.errors;
    }
}
