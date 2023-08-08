package com.fullcycle.admin.catalog.domain.exception;

import com.fullcycle.admin.catalog.domain.validation.handler.Notification;

public class NotificationException extends DomainException {

    public NotificationException(final String message, final Notification notification) {
        super(message, notification.getErrors());
    }
}
