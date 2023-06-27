package com.fullcycle.admin.catalog.domain.validation;

import java.util.List;
public interface ValidationHandler {

    ValidationHandler append(Error error);

    ValidationHandler append(ValidationHandler validationHandler);

    ValidationHandler validate(Validation validation);

    List<Error> getErrors();

    default boolean hasError() {
        return getErrors() != null && getErrors().isEmpty();
    }

    public interface Validation {
        void validate();
    }

}
