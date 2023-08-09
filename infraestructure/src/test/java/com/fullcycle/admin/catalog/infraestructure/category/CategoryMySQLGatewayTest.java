package com.fullcycle.admin.catalog.infraestructure.category;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.MySQLGatewayTest;
import com.fullcycle.admin.catalog.infraestructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalog.infraestructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@MySQLGatewayTest
public class CategoryMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAValidCategory_whenCallsCreate_thenReturnANewCategory() {
        final var expectedName = "Filme";
        final var expectedDescription = "Um filme";
        final var expectedIsActive = true;
        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        Assertions.assertEquals(0, categoryRepository.count());

        final var response = categoryGateway.create(category);

        Assertions.assertEquals(1, categoryRepository.count());
        Assertions.assertEquals(category.getId(), response.getId());
        Assertions.assertEquals(expectedName, response.getName());
        Assertions.assertEquals(expectedDescription, response.getDescription());
        Assertions.assertEquals(expectedIsActive, response.isActive());
        Assertions.assertEquals(category.getCreatedAt(), response.getCreatedAt());
        Assertions.assertEquals(category.getUpdatedAt(), response.getUpdatedAt());
        Assertions.assertEquals(category.getDeletedAt(), response.getDeletedAt());

        final var persistedEntity = categoryRepository.findById(category.getId().getValue()).get();
        Assertions.assertEquals(category.getId().getValue(), persistedEntity.getId());
        Assertions.assertEquals(expectedName, persistedEntity.getName());
        Assertions.assertEquals(expectedDescription, persistedEntity.getDescription());
        Assertions.assertEquals(expectedIsActive, persistedEntity.isActive());
        Assertions.assertEquals(category.getCreatedAt(), persistedEntity.getCreatedAt());
        Assertions.assertEquals(category.getUpdatedAt(), persistedEntity.getUpdatedAt());
        Assertions.assertEquals(category.getDeletedAt(), persistedEntity.getDeletedAt());
    }

    @Test
    public void givenAValidCategory_whenCallsUpdate_thenReturnACategoryUpdated() {
        final var expectedName = "Filme";
        final var expectedDescription = "Um filme";
        final var expectedIsActive = true;
        final var category = Category.newCategory("", null, false);

        Assertions.assertEquals(0, categoryRepository.count());
        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));

        final var updatedCategory = category.clone().update(expectedName, expectedDescription, expectedIsActive);

        final var response = categoryGateway.update(updatedCategory);

        Assertions.assertEquals(1, categoryRepository.count());
        Assertions.assertEquals(category.getId(), response.getId());
        Assertions.assertEquals(expectedName, response.getName());
        Assertions.assertEquals(expectedDescription, response.getDescription());
        Assertions.assertEquals(expectedIsActive, response.isActive());
        Assertions.assertTrue(category.getUpdatedAt().isBefore(response.getUpdatedAt()));
        Assertions.assertEquals(category.getCreatedAt(), response.getCreatedAt());
        Assertions.assertNull(response.getDeletedAt());

        final var persistedEntity = categoryRepository.findById(category.getId().getValue()).get();

        Assertions.assertEquals(category.getId().getValue(), persistedEntity.getId());
        Assertions.assertEquals(expectedName, persistedEntity.getName());
        Assertions.assertEquals(expectedDescription, persistedEntity.getDescription());
        Assertions.assertEquals(expectedIsActive, persistedEntity.isActive());
        Assertions.assertTrue(category.getUpdatedAt().isBefore(persistedEntity.getUpdatedAt()));
        Assertions.assertEquals(category.getCreatedAt(), response.getCreatedAt());
        Assertions.assertNull(response.getDeletedAt());
    }

    @Test
    public void givenAPrePersistedCategoryAndValidCategoryId_whenTryToDeleteIt_thenDeleteCategory() {
        final var category = Category.newCategory("Filme", "O filme mais assistido", false);
        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));

        categoryGateway.deleteById(category.getId());

        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenInvalidCategoryId_whenTryToDeleteIt_shouldDeleteCategory() {
        categoryGateway.deleteById(CategoryID.from("invalid"));

        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAPrePersistedCategoryAndValidCategoryId_whenCallsFindById_thenReturnCategory() {
        final var category = Category.newCategory("Filme", "O filme mais assistido", true);
        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));

        final var response = categoryGateway.findById(category.getId()).get();

        Assertions.assertEquals(category.getId(), response.getId());
        Assertions.assertEquals(category.getName(), response.getName());
        Assertions.assertEquals(category.getDescription(), response.getDescription());
        Assertions.assertEquals(category.isActive(), response.isActive());
        Assertions.assertEquals(category.getCreatedAt(), response.getCreatedAt());
        Assertions.assertEquals(category.getUpdatedAt(), response.getUpdatedAt());
        Assertions.assertEquals(category.getDeletedAt(), response.getDeletedAt());
    }

    @Test
    public void givenValidCategoryIdNotStored_whenCallsFindById_thenReturnEmpty() {
        final var response = categoryGateway.findById(CategoryID.from("empty"));

        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    public void givenPrePersistedCategories_whenCallsFindAll_thenReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;
        final var movie = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentary = Category.newCategory("Documentário", null, true);
        categoryRepository.saveAll(List.of(
                        CategoryJpaEntity.from(movie),
                        CategoryJpaEntity.from(series),
                        CategoryJpaEntity.from(documentary)
                )
        );
        final var query = new SearchQuery(0, 1, "", "name", "asc");

        final var response = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, response.currentPage());
        Assertions.assertEquals(expectedPerPage, response.perPage());
        Assertions.assertEquals(expectedTotal, response.total());
        Assertions.assertEquals(expectedPerPage, response.items().size());
        Assertions.assertEquals(documentary.getId(), response.items().get(0).getId());
    }

    @Test
    public void givenEmptyCategoriesTable_whenCallsFindAll_returnEmptyPage() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 0;
        final var query = new SearchQuery(0, 1, "", "name", "asc");

        final var response = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, response.currentPage());
        Assertions.assertEquals(expectedPerPage, response.perPage());
        Assertions.assertEquals(expectedTotal, response.total());
        Assertions.assertEquals(0, response.items().size());
    }

    @Test
    public void givenFollowPagination_whenCallsFindAllWithPage1_thenReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 3;
        final var expectedTotal = 3;
        final var movie = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentary = Category.newCategory("Documentário", null, true);
        categoryRepository.saveAll(List.of(
                        CategoryJpaEntity.from(movie),
                        CategoryJpaEntity.from(series),
                        CategoryJpaEntity.from(documentary)
                )
        );
        final var query = new SearchQuery(0, 3, "", "name", "asc");

        final var response = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, response.currentPage());
        Assertions.assertEquals(expectedPerPage, response.perPage());
        Assertions.assertEquals(expectedTotal, response.total());
        Assertions.assertEquals(expectedPerPage, response.items().size());
        Assertions.assertEquals(documentary.getId(), response.items().get(0).getId());
        Assertions.assertEquals(movie.getId(), response.items().get(1).getId());
        Assertions.assertEquals(series.getId(), response.items().get(2).getId());
    }

    @Test
    public void givenPrePersistedCategoriesAndDocAsTerms_whenCallsFindAllAndTermsMatchesCategoryName_thenReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;
        final var movie = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentary = Category.newCategory("Documentário", null, true);
        categoryRepository.saveAll(List.of(
                        CategoryJpaEntity.from(movie),
                        CategoryJpaEntity.from(series),
                        CategoryJpaEntity.from(documentary)
                )
        );
        final var query = new SearchQuery(0, 1, "doc", "name", "asc");

        final var response = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, response.currentPage());
        Assertions.assertEquals(expectedPerPage, response.perPage());
        Assertions.assertEquals(expectedTotal, response.total());
        Assertions.assertEquals(expectedPerPage, response.items().size());
        Assertions.assertEquals(documentary.getId(), response.items().get(0).getId());
    }

    @Test
    public void givenPrePersistedCategoriesAndMaisAssistidaAsTerms_whenCallsFindAllAndTermsMatchesCategoryDescription_thenReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;
        final var movie = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var series = Category.newCategory("Séries", "Uma categoria assistida", true);
        final var documentary = Category.newCategory("Documentário", "A categoria menos assistida", true);
        categoryRepository.saveAll(List.of(
                        CategoryJpaEntity.from(movie),
                        CategoryJpaEntity.from(series),
                        CategoryJpaEntity.from(documentary)
                )
        );
        final var query = new SearchQuery(0, 1, "mais assistida", "name", "asc");

        final var response = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, response.currentPage());
        Assertions.assertEquals(expectedPerPage, response.perPage());
        Assertions.assertEquals(expectedTotal, response.total());
        Assertions.assertEquals(expectedPerPage, response.items().size());
        Assertions.assertEquals(movie.getId(), response.items().get(0).getId());
    }
}
