package com.fullcycle.admin.catalog.infraestructure.genre;

import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.infraestructure.genre.persistance.GenreJpaEntity;
import com.fullcycle.admin.catalog.infraestructure.genre.persistance.GenreRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class GenreMySQLGateway implements GenreGateway {

    private final GenreRepository repository;

    public GenreMySQLGateway(final GenreRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Genre create(Genre genre) {
        return save(genre);
    }

    @Override
    public void deleteById(final GenreID id) {
        final var genreID = id.getValue();

        if(repository.existsById(genreID)) {
            repository.deleteById(genreID);
        }
    }

    @Override
    public Optional<Genre> findById(GenreID id) {
        return Optional.empty();
    }

    @Override
    public Genre update(final Genre genre) {
        return save(genre);
    }

    @Override
    public Pagination<Genre> findAll(SearchQuery searchQuery) {
        return null;
    }

    private Genre save(Genre genre) {
        return repository.save(GenreJpaEntity.from(genre)).toAggregate();
    }
}
