package com.fullcycle.admin.catalog.infraestructure.category;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.infraestructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalog.infraestructure.category.persistence.CategoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static com.fullcycle.admin.catalog.infraestructure.utils.SpecificationUtils.like;

@Component
public class CategoryMySQLGateway implements CategoryGateway {

    private final CategoryRepository repository;

    public CategoryMySQLGateway(final CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Category create(final Category category) {
        return save(category);
    }

    private Category save(final Category category) {
        return repository.save(CategoryJpaEntity.from(category))
                .toAggregate();
    }

    @Override
    public void deleteById(final CategoryID id) {
        final var idValue = id.getValue();
        if(repository.existsById(idValue)) {
            repository.deleteById(idValue);
        }
    }

    @Override
    public Optional<Category> findById(final CategoryID id) {
        return repository.findById(id.getValue()).map(CategoryJpaEntity::toAggregate);
    }

    @Override
    public Category update(final Category category) {
        return save(category);
    }

    @Override
    public Pagination<Category> findAll(final SearchQuery query) {
        final var page = PageRequest.of(
                query.page(),
                query.perPage(),
                Sort.by(Direction.fromString(query.direction()), query.sort())
        );

        final var specification = Optional.ofNullable(query.terms())
                .filter(str -> !str.isBlank())
                .map(this::assembleSpecification)
                .orElse(null);

        var pageResult = repository.findAll(Specification.where(specification), page);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(CategoryJpaEntity::toAggregate).toList()
        );
    }

    private Specification<CategoryJpaEntity> assembleSpecification(final String terms) {
        final Specification<CategoryJpaEntity> nameLike = like("name", terms);
        final Specification<CategoryJpaEntity> descriptionLike = like("description", terms);
        return nameLike.or(descriptionLike);
    }

    @Override
    public List<CategoryID> existsByIds(Iterable<CategoryID> categoryIDS) {
        final var ids = StreamSupport.stream(categoryIDS.spliterator(), false)
                .map(CategoryID::getValue)
                .toList();

        return repository.existsByIds(ids).stream()
                .map(CategoryID::from)
                .toList();
    }
}
