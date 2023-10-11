package com.fullcycle.admin.catalog.infraestructure.genre;

import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.infraestructure.genre.persistence.GenreJpaEntity;
import com.fullcycle.admin.catalog.infraestructure.genre.persistence.GenreRepository;
import com.fullcycle.admin.catalog.infraestructure.utils.SpecificationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.springframework.data.jpa.domain.Specification.where;

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
    public Optional<Genre> findById(final GenreID id) {
        return repository.findById(id.getValue())
                .map(GenreJpaEntity::toAggregate);
    }

    @Override
    public Genre update(final Genre genre) {
        return save(genre);
    }

    @Override
    public Pagination<Genre> findAll(final SearchQuery query) {
        final var page = PageRequest.of(
                query.page(),
                query.perPage(),
                Sort.by(Sort.Direction.fromString(query.direction()), query.sort())
        );

        final var where = Optional.ofNullable(query.terms())
                        .filter(str -> !str.isBlank())
                        .map(this::assembleSpecification)
                        .orElse(null);

        final var pageResult = repository.findAll(where(where), page);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(GenreJpaEntity::toAggregate).toList()
        );
    }

    @Override
    public List<GenreID> existsByIds(final Iterable<GenreID> ids) {
        final var genreIDS = StreamSupport.stream(ids.spliterator(), false)
                .map(GenreID::getValue)
                .toList();
        return this.repository.existsByIds(genreIDS).stream()
                .map(GenreID::from)
                .toList();
    }

    private Specification<GenreJpaEntity> assembleSpecification(final String terms) {
        return SpecificationUtils.like("name", terms);
    }

    private Genre save(Genre genre) {
        return repository.save(GenreJpaEntity.from(genre)).toAggregate();
    }
}
