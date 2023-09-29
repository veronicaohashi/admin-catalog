package com.fullcycle.admin.catalog.domain.exception;

import com.fullcycle.admin.catalog.domain.validation.Error;

import java.util.List;

public class InternalErrorException extends NoStacktraceException {
    protected InternalErrorException(final String message, final Throwable t) {
        super(message, t);
    }

    public static InternalErrorException with(final String message, final Throwable t) {
        return new InternalErrorException(message, t);
    }
}
