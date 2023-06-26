package com.fullcycle.admin.catalog.domain.application;

import com.fullcycle.admin.catalog.domain.category.Category;

public class UseCase {
    public Category execute() {
        return Category.newCategory("Filmes", "A categoria mais assistida", true);
    }
}