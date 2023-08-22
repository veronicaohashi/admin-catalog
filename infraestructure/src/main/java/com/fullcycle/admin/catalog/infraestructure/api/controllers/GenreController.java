package com.fullcycle.admin.catalog.infraestructure.api.controllers;

import com.fullcycle.admin.catalog.application.genre.create.CreateGenreCommand;
import com.fullcycle.admin.catalog.application.genre.create.CreateGenreUseCase;
import com.fullcycle.admin.catalog.application.genre.delete.DeleteGenreUseCase;
import com.fullcycle.admin.catalog.application.genre.retrieve.get.GetGenreByIdUseCase;
import com.fullcycle.admin.catalog.application.genre.retrieve.list.ListGenreUseCase;
import com.fullcycle.admin.catalog.application.genre.update.UpdateGenreCommand;
import com.fullcycle.admin.catalog.application.genre.update.UpdateGenreUseCase;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.infraestructure.api.GenreAPI;
import com.fullcycle.admin.catalog.infraestructure.genre.models.CreateGenreRequest;
import com.fullcycle.admin.catalog.infraestructure.genre.models.GenreListResponse;
import com.fullcycle.admin.catalog.infraestructure.genre.models.GenreResponse;
import com.fullcycle.admin.catalog.infraestructure.genre.models.UpdateGenreRequest;
import com.fullcycle.admin.catalog.infraestructure.genre.presenters.GenreApiPresenter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
public class GenreController implements GenreAPI {

    private final CreateGenreUseCase createGenreUseCase;

    private final GetGenreByIdUseCase getGenreByIdUseCase;

    private final UpdateGenreUseCase updateGenreUseCase;

    private final DeleteGenreUseCase deleteGenreUseCase;

    private final ListGenreUseCase listGenreUseCase;

    public GenreController(
            final CreateGenreUseCase createGenreUseCase,
            final GetGenreByIdUseCase getGenreByIdUseCase,
            final UpdateGenreUseCase updateGenreUseCase,
            final DeleteGenreUseCase deleteGenreUseCase,
            final ListGenreUseCase listGenreUseCase
            ) {
        this.createGenreUseCase = Objects.requireNonNull(createGenreUseCase);
        this.getGenreByIdUseCase = Objects.requireNonNull(getGenreByIdUseCase);
        this.updateGenreUseCase = Objects.requireNonNull(updateGenreUseCase);
        this.deleteGenreUseCase = Objects.requireNonNull(deleteGenreUseCase);
        this.listGenreUseCase = Objects.requireNonNull(listGenreUseCase);
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
    public Pagination<GenreListResponse> list(
            final String search,
            final int page,
            final int perPage,
            final String sort,
            final String direction
    ) {
        return listGenreUseCase.execute(new SearchQuery(page, perPage, search, sort, direction))
                .map(GenreApiPresenter::present);
    }

    @Override
    public GenreResponse getById(final String id) {
        return GenreApiPresenter.present(getGenreByIdUseCase.execute(id));
    }

    @Override
    public ResponseEntity<?> update(final String id, final UpdateGenreRequest input) {
        final var command = UpdateGenreCommand.with(
                id,
                input.name(),
                input.active(),
                input.categories()
        );
        return ResponseEntity.ok(updateGenreUseCase.execute(command));
    }

    @Override
    public void delete(final String id) {
        deleteGenreUseCase.execute(id);
    }
}
