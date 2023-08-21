package com.fullcycle.admin.catalog.infraestructure.api.controllers;

import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.infraestructure.api.GenreAPI;
import com.fullcycle.admin.catalog.infraestructure.genre.models.CreateGenreRequest;
import com.fullcycle.admin.catalog.infraestructure.genre.models.GenreListResponse;
import com.fullcycle.admin.catalog.infraestructure.genre.models.GenreResponse;
import com.fullcycle.admin.catalog.infraestructure.genre.models.UpdateGenreRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GenreController implements GenreAPI {
    @Override
    public ResponseEntity<?> create(CreateGenreRequest input) {
        return null;
    }

    @Override
    public Pagination<GenreListResponse> list(String search, int page, int perPage, String sort, String direction) {
        return null;
    }

    @Override
    public GenreResponse getById(String id) {
        return null;
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateGenreRequest input) {
        return null;
    }

    @Override
    public void delete(String id) {

    }
}
