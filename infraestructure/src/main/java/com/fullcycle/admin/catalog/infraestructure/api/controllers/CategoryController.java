package com.fullcycle.admin.catalog.infraestructure.api.controllers;

import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.infraestructure.api.CategoryAPI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController implements CategoryAPI {
    @Override
    public ResponseEntity<?> create() {
        return null;
    }

    @Override
    public Pagination<?> list(String search, int page, int perPage, String sort, String direction) {
        return null;
    }
}
