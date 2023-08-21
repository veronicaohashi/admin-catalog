package com.fullcycle.admin.catalog.infraestructure.api.controllers;

import com.fullcycle.admin.catalog.application.genre.create.CreateGenreCommand;
import com.fullcycle.admin.catalog.application.genre.create.CreateGenreUseCase;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.infraestructure.api.GenreAPI;
import com.fullcycle.admin.catalog.infraestructure.genre.models.CreateGenreRequest;
import com.fullcycle.admin.catalog.infraestructure.genre.models.GenreListResponse;
import com.fullcycle.admin.catalog.infraestructure.genre.models.GenreResponse;
import com.fullcycle.admin.catalog.infraestructure.genre.models.UpdateGenreRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class GenreController implements GenreAPI {

    private final CreateGenreUseCase createGenreUseCase;

    public GenreController(final CreateGenreUseCase createGenreUseCase) {
        this.createGenreUseCase = createGenreUseCase;
    }

    @Override
    public ResponseEntity<?> create(final CreateGenreRequest input) {
        final var command = CreateGenreCommand.with(
                input.name(),
                input.active(),
                input.categories()
        );

        final var output = createGenreUseCase.execute(command);

        return ResponseEntity.created(URI.create("/genres/" + output.id())).body(output);
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
