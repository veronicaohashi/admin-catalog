package com.fullcycle.admin.catalog.application;

public abstract class UseCase<IN, OUT> {
    public abstract OUT execute(IN in);
}