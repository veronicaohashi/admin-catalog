package com.fullcycle.admin.catalog.infraestructure.genre;

import com.fullcycle.admin.catalog.MySQLGatewayTest;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.infraestructure.genre.persistance.GenreJpaEntity;
import com.fullcycle.admin.catalog.infraestructure.genre.persistance.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@MySQLGatewayTest
class GenreMySQLGatewayTest {

    @Autowired
    private CategoryGateway categoryGateway;

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void givenAValidGenre_whenCallsCreateGenre_thenPersistGenre() {
        final var category = categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var expectedName = "Terror";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(category.getId());
        final var genre = Genre.newGenre(expectedName, expectedIsActive);
        genre.addCategories(expectedCategories);
        final var expectedId = genre.getId();

        final var result = genreGateway.create(genre);

        Assertions.assertEquals(expectedId, result.getId());
        Assertions.assertEquals(expectedName, result.getName());
        Assertions.assertEquals(expectedIsActive, result.isActive());
        Assertions.assertEquals(expectedCategories, result.getCategories());
        Assertions.assertEquals(genre.getCreatedAt(), result.getCreatedAt());
        Assertions.assertEquals(genre.getUpdatedAt(), result.getUpdatedAt());
        Assertions.assertNull(result.getDeletedAt());

        final var persistedGenre = genreRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedName, persistedGenre.getName());
        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertEquals(expectedCategories, persistedGenre.getCategoryIDs());
        Assertions.assertEquals(genre.getCreatedAt(), persistedGenre.getCreatedAt());
        Assertions.assertEquals(genre.getUpdatedAt(), persistedGenre.getUpdatedAt());
        Assertions.assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    void givenAValidGenreWithoutCategories_whenCallsCreateGenre_thenPersistGenre() {
        final var expectedName = "Terror";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var genre = Genre.newGenre(expectedName, expectedIsActive);
        final var expectedId = genre.getId();

        final var result = genreGateway.create(genre);

        Assertions.assertEquals(expectedId, result.getId());
        Assertions.assertEquals(expectedName, result.getName());
        Assertions.assertEquals(expectedIsActive, result.isActive());
        Assertions.assertEquals(expectedCategories, result.getCategories());
        Assertions.assertEquals(genre.getCreatedAt(), result.getCreatedAt());
        Assertions.assertEquals(genre.getUpdatedAt(), result.getUpdatedAt());
        Assertions.assertNull(result.getDeletedAt());

        final var persistedGenre = genreRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedName, persistedGenre.getName());
        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertEquals(expectedCategories, persistedGenre.getCategoryIDs());
        Assertions.assertEquals(genre.getCreatedAt(), persistedGenre.getCreatedAt());
        Assertions.assertEquals(genre.getUpdatedAt(), persistedGenre.getUpdatedAt());
        Assertions.assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    void givenAValidGenreWithoutCategories_whenCallsUpdateGenreWithCategories_thenPersistGenre() {
        final var category = categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var expectedCategories = List.of(category.getId());
        final var expectedName = "Terror";
        final var expectedIsActive = true;
        final var genre = Genre.newGenre("Ação", true);
        final var expectedId = genre.getId();

        Assertions.assertEquals(0, genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(genre));

        Assertions.assertEquals(0, genre.getCategories().size());

        final var result = genreGateway.update(Genre.with(genre).update(expectedName, expectedIsActive, expectedCategories));

        Assertions.assertEquals(expectedId, result.getId());
        Assertions.assertEquals(expectedName, result.getName());
        Assertions.assertEquals(expectedIsActive, result.isActive());
        Assertions.assertEquals(expectedCategories, result.getCategories());
        Assertions.assertEquals(genre.getCreatedAt(), result.getCreatedAt());
        Assertions.assertTrue(genre.getUpdatedAt().isBefore(result.getUpdatedAt()));
        Assertions.assertNull(result.getDeletedAt());

        final var persistedGenre = genreRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedName, persistedGenre.getName());
        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertEquals(expectedCategories, persistedGenre.getCategoryIDs());
        Assertions.assertEquals(genre.getCreatedAt(), persistedGenre.getCreatedAt());
        Assertions.assertTrue(genre.getUpdatedAt().isBefore(persistedGenre.getUpdatedAt()));
        Assertions.assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    void givenAValidGenreWithCategories_whenCallsUpdateGenreCleaningCategories_thenPersistGenre() {
        final var category = categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var expectedCategories = List.<CategoryID>of();
        final var genre = Genre.newGenre("Terror", true);
        genre.addCategories(List.of(category.getId()));

        genreRepository.saveAndFlush(GenreJpaEntity.from(genre));

        final var result = genreGateway.update(Genre.with(genre).update(genre.getName(), genre.isActive(), expectedCategories));

        Assertions.assertEquals(expectedCategories, result.getCategories());

        final var persistedGenre = genreRepository.findById(genre.getId().getValue()).get();

        Assertions.assertEquals(expectedCategories, persistedGenre.getCategoryIDs());
    }

    @Test
    void givenAValidGenreInactive_whenCallsUpdateGenreActivating_thenPersistGenre() {
        final var genre = Genre.newGenre("Terror", false);
        final var expectedIsActive = true;

        genreRepository.saveAndFlush(GenreJpaEntity.from(genre));

        final var result = genreGateway.update(Genre.with(genre).update(genre.getName(), expectedIsActive, genre.getCategories()));

        Assertions.assertEquals(expectedIsActive, result.isActive());
        Assertions.assertNull(result.getDeletedAt());

        final var persistedGenre = genreRepository.findById(genre.getId().getValue()).get();

        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    void givenAValidGenreActive_whenCallsUpdateGenreInactivating_thenPersistGenre() {
        final var genre = Genre.newGenre("Terror", true);
        final var expectedIsActive = false;

        genreRepository.saveAndFlush(GenreJpaEntity.from(genre));

        final var result = genreGateway.update(Genre.with(genre).update(genre.getName(), expectedIsActive, genre.getCategories()));

        Assertions.assertEquals(expectedIsActive, result.isActive());
        Assertions.assertNotNull(result.getDeletedAt());

        final var persistedGenre = genreRepository.findById(genre.getId().getValue()).get();

        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertNotNull(persistedGenre.getDeletedAt());
    }

    @Test
    void givenAPrePersistedGenre_whenCallsDeleteById_thenDeleteGenre() {
        final var genre = Genre.newGenre("Terror", true);
        final var expectedId = genre.getId();

        genreRepository.saveAndFlush(GenreJpaEntity.from(genre));

        genreGateway.deleteById(expectedId);

        Assertions.assertEquals(0, genreRepository.count());
    }

    @Test
    void givenAnInvalidGenre_whenCallsDeleteById_thenReturnOK() {
        genreGateway.deleteById(GenreID.from("123"));

        Assertions.assertEquals(0, genreRepository.count());

    }
}
