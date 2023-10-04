package com.fullcycle.admin.catalog.domain.exception;

public class InternalErrorException extends NoStacktraceException {
    protected InternalErrorException(final String message, final Throwable t) {
        super(message, t);
    }

    public static InternalErrorException with(final String message, final Throwable t) {
        return new InternalErrorException(message, t);
    }
}
