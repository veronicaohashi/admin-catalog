package com.fullcycle.admin.catalog.e2e.genre;

import com.fullcycle.admin.catalog.E2ETest;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.e2e.MockDsl;
import com.fullcycle.admin.catalog.infraestructure.genre.models.UpdateGenreRequest;
import com.fullcycle.admin.catalog.infraestructure.genre.persistance.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
@Testcontainers
public class GenreE2ETest implements MockDsl {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private GenreRepository genreRepository;

    @Override
    public MockMvc mvc() {
        return this.mvc;
    }

    @Container
    private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer("mysql:latest")
            .withPassword("123456")
            .withUsername("root")
            .withDatabaseName("admin_videos");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("mysql.port", () -> MY_SQL_CONTAINER.getMappedPort(3306));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToCreateANewGenreWithValidValues() throws Exception {
        final var expectedName = "Terror";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var genreId = givenAGenre(expectedName, expectedIsActive, expectedCategories);

        final var genre = genreRepository.findById(genreId.getValue()).get();

        Assertions.assertEquals(expectedName, genre.getName());
        Assertions.assertEquals(expectedIsActive, genre.isActive());
        Assertions.assertTrue(expectedCategories.containsAll(genre.getCategoryIDs()));
        Assertions.assertNotNull(genre.getCreatedAt());
        Assertions.assertNotNull(genre.getUpdatedAt());
        Assertions.assertNull(genre.getDeletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToCreateANewGenreWithCategories() throws Exception {
        final var filmes = givenACategory("Filmes", null, true);
        final var expectedName = "Terror";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes);
        final var genreId = givenAGenre(expectedName, expectedIsActive, expectedCategories);

        final var genre = genreRepository.findById(genreId.getValue()).get();

        Assertions.assertTrue(expectedCategories.containsAll(genre.getCategoryIDs()));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToNavigateThruAllGenres() throws Exception {
        givenAGenre("Ação", true, List.of());
        givenAGenre("Esportes", true, List.of());
        givenAGenre("Drama", true, List.of());

        listGenres(0, 1)
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Ação")));

        listGenres(1, 1)
                .andExpect(jsonPath("$.current_page", equalTo(1)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Drama")));

        listGenres(2, 1)
                .andExpect(jsonPath("$.current_page", equalTo(2)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Esportes")));

        listGenres(3, 1)
                .andExpect(jsonPath("$.current_page", equalTo(3)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(0)));

    }

    @Test
    void asACatalogAdminIShouldBeAbleToSearchBetweenAllGenres() throws Exception {
        givenAGenre("Ação", true, List.of());
        givenAGenre("Esportes", true, List.of());
        givenAGenre("Drama", true, List.of());

        listGenres(0, 1, "dra")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(1)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Drama")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSortAllGenresByNameDesc() throws Exception {
        givenAGenre("Ação", true, List.of());
        givenAGenre("Esportes", true, List.of());
        givenAGenre("Drama", true, List.of());

        listGenres(0, 3, "", "name", "desc")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(3)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Esportes")))
                .andExpect(jsonPath("$.items[1].name", equalTo("Drama")))
                .andExpect(jsonPath("$.items[2].name", equalTo("Ação")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToGetAGenreByItsIdentifier() throws Exception {
        final var filmes = givenACategory("Filmes", null, true);
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes);

        final var genreId = givenAGenre(expectedName, expectedIsActive, expectedCategories);

        final var genre = retrieveAGenre(genreId);

        Assertions.assertEquals(expectedName, genre.name());
        Assertions.assertEquals(expectedIsActive, genre.active());
        Assertions.assertTrue(mapTo(expectedCategories, CategoryID::getValue).containsAll(genre.categories()));
        Assertions.assertNotNull(genre.createdAt());
        Assertions.assertNotNull(genre.updatedAt());
        Assertions.assertNull(genre.deletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSeeATreatedErrorByGettingANotFoundCategory() throws Exception {
        final var request = get("/genres/123")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo("Genre with ID 123 was not found")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToUpdateAGenreByItsIdentifier() throws Exception {
        final var filmes = givenACategory("Filmes", "A categoria mais assistida", true);
        final var expectedName = "Terror";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes);
        final var genreId = givenAGenre("Ação", false, List.of());

        final var requestBody = new UpdateGenreRequest(expectedName, mapTo(expectedCategories, CategoryID::getValue), expectedIsActive);

        updateAGenre(genreId, requestBody)
                .andExpect(status().isOk());

        final var genre = genreRepository.findById(genreId.getValue()).get();

        Assertions.assertEquals(expectedName, genre.getName());
        Assertions.assertEquals(expectedIsActive, genre.isActive());
        Assertions.assertTrue(expectedCategories.containsAll(genre.getCategoryIDs()));
        Assertions.assertNotNull(genre.getCreatedAt());
        Assertions.assertNotNull(genre.getUpdatedAt());
        Assertions.assertNull(genre.getDeletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToDeleteAGenreByItsIdentifier() throws Exception {
        final var genreId = givenAGenre("Ação", false, List.of());

        deleteAGenre(genreId)
                .andExpect(status().isNoContent());

        Assertions.assertFalse(this.genreRepository.existsById(genreId.getValue()));
    }
}
