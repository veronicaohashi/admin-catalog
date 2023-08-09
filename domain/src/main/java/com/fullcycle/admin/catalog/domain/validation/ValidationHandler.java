package com.fullcycle.admin.catalog.domain.validation;

import java.util.List;

public interface ValidationHandler {

    ValidationHandler append(Error error);

    ValidationHandler append(ValidationHandler validationHandler);

    <T> T validate(Validation<T> validation);

    List<Error> getErrors();

    default boolean hasError() {
        return getErrors() != null && !getErrors().isEmpty();
    }

    default Error firstError() {
        if (getErrors() != null && !getErrors().isEmpty()) {
            return getErrors().get(0);
        } else {
            return null;
        }
    }

    interface Validation<T> {
        T validate();
    }

}
