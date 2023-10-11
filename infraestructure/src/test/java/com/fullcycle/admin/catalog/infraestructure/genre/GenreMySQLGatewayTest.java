package com.fullcycle.admin.catalog.infraestructure.genre;

import com.fullcycle.admin.catalog.MySQLGatewayTest;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.infraestructure.genre.persistence.GenreJpaEntity;
import com.fullcycle.admin.catalog.infraestructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @Test
    void givenAPrePersistedGenre_whenCallsFindById_thenReturnGenre() {
        final var series =
                categoryGateway.create(Category.newCategory("Séries", null, true));
        final var expectedName = "Terror";
        final var expectedIsActive = true;
        final var genre = Genre.newGenre(expectedName, expectedIsActive);
        final var expectedCategories = List.of(series.getId());
        final var expectedId = genre.getId();
        genre.addCategories(expectedCategories);

        genreRepository.saveAndFlush(GenreJpaEntity.from(genre));

        final var result = genreGateway.findById(expectedId).get();

        Assertions.assertEquals(expectedId, result.getId());
        Assertions.assertEquals(expectedName, result.getName());
        Assertions.assertEquals(expectedIsActive, result.isActive());
        Assertions.assertEquals(expectedCategories, result.getCategories());
        Assertions.assertEquals(genre.getCreatedAt(), result.getCreatedAt());
        Assertions.assertEquals(genre.getUpdatedAt(), result.getUpdatedAt());
        Assertions.assertNull(result.getDeletedAt());
    }

    @Test
    void givenAInvalidGenreId_whenCallsFindById_thenReturnEmpty() {
        final var result = genreGateway.findById(GenreID.from("1234"));

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void givenEmptyGenres_whenCallFindAll_thenReturnEmptyList() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var result = genreGateway.findAll(query);

        Assertions.assertEquals(expectedPage, result.currentPage());
        Assertions.assertEquals(expectedPerPage, result.perPage());
        Assertions.assertEquals(expectedTotal, result.total());
        Assertions.assertEquals(expectedTotal, result.items().size());
    }

    @ParameterizedTest
    @CsvSource({
            "aç,0,10,1,1,Ação",
            "dr,0,10,1,1,Drama",
            "com,0,10,1,1,Comédia romântica",
            "cien,0,10,1,1,Ficção científica",
            "terr,0,10,1,1,Terror",
    })
    void givenAValidTerm_whenCallsFindAll_thenReturnFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedGenreName
    ){
        mockGenres();
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var result = genreGateway.findAll(query);

        Assertions.assertEquals(expectedPage, result.currentPage());
        Assertions.assertEquals(expectedPerPage, result.perPage());
        Assertions.assertEquals(expectedTotal, result.total());
        Assertions.assertEquals(expectedItemsCount, result.items().size());
        Assertions.assertEquals(expectedGenreName, result.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,5,5,Ação",
            "name,desc,0,10,5,5,Terror",
            "createdAt,asc,0,10,5,5,Comédia romântica",
            "createdAt,desc,0,10,5,5,Ficção científica",
    })
    void givenAValidSortAndDirection_whenCallsFindAll_thenReturnOrdered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedGenreName
    ) {
        mockGenres();
        final var expectedTerms = "";

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var result = genreGateway.findAll(query);

        Assertions.assertEquals(expectedPage, result.currentPage());
        Assertions.assertEquals(expectedPerPage, result.perPage());
        Assertions.assertEquals(expectedTotal, result.total());
        Assertions.assertEquals(expectedItemsCount, result.items().size());
        Assertions.assertEquals(expectedGenreName, result.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,5,Ação;Comédia romântica",
            "1,2,2,5,Drama;Ficção científica",
            "2,2,1,5,Terror",
    })
    void givenAValidSortAndDirection_whenCallsFindAll_thenReturnPaged(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedGenres
    ) {
        mockGenres();
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var result = genreGateway.findAll(query);

        Assertions.assertEquals(expectedPage, result.currentPage());
        Assertions.assertEquals(expectedPerPage, result.perPage());
        Assertions.assertEquals(expectedTotal, result.total());
        Assertions.assertEquals(expectedItemsCount, result.items().size());

        int index = 0;
        for (final var expectedName : expectedGenres.split(";")) {
            final var actualName = result.items().get(index).getName();
            Assertions.assertEquals(expectedName, actualName);
            index++;
        }
    }

    private void mockGenres() {
        genreRepository.saveAllAndFlush(List.of(
                GenreJpaEntity.from(Genre.newGenre("Comédia romântica", true)),
                GenreJpaEntity.from(Genre.newGenre("Ação", true)),
                GenreJpaEntity.from(Genre.newGenre("Drama", true)),
                GenreJpaEntity.from(Genre.newGenre("Terror", true)),
                GenreJpaEntity.from(Genre.newGenre("Ficção científica", true))
        ));
    }
}
