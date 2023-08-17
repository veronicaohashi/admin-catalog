package com.fullcycle.admin.catalog.infraestructure.genre;

import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GenreMySQLGateway implements GenreGateway {
    @Override
    public Genre create(Genre genre) {
        return null;
    }

    @Override
    public void deleteById(GenreID id) {

    }

    @Override
    public Optional<Genre> findById(GenreID id) {
        return Optional.empty();
    }

    @Override
    public Genre update(Genre genre) {
        return null;
    }

    @Override
    public Pagination<Genre> findAll(SearchQuery searchQuery) {
        return null;
    }
}
