package com.fullcycle.admin.catalog.application.genre.retrieve.get;

import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;

import java.util.Objects;

public class DefaultGetGenreByIdUseCase extends GetGenreByIdUseCase {
    private final GenreGateway genreGateway;

    public DefaultGetGenreByIdUseCase(final GenreGateway genreGateway) {
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public GenreOutput execute(final String id) {
        final var genreId = GenreID.from(id);
        return genreGateway.findById(genreId)
                .map(GenreOutput::from)
                .orElseThrow(() -> NotFoundException.with(Genre.class, genreId));
    }
}
