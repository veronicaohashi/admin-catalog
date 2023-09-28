package com.fullcycle.admin.catalog.domain.genre;

import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;

import java.util.List;
import java.util.Optional;

public interface GenreGateway {

    Genre create(Genre genre);

    void deleteById(GenreID id);

    Optional<Genre> findById(GenreID id);

    Genre update(Genre genre);

    Pagination<Genre> findAll(SearchQuery searchQuery);

    List<GenreID> existsByIds(final Iterable<GenreID> ids);
}
