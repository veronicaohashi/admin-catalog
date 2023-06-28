package com.fullcycle.admin.catalog.domain.application;

public abstract class UseCase<IN, OUT> {
    public abstract OUT execute(IN in);
}