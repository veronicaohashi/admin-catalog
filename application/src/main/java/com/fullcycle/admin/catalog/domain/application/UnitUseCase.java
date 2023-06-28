package com.fullcycle.admin.catalog.domain.application;

public abstract class UnitUseCase<IN> {
    public abstract void execute(IN in);
}
