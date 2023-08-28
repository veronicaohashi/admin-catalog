package com.fullcycle.admin.catalog.e2e.genre;

import com.fullcycle.admin.catalog.E2ETest;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.infraestructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalog.infraestructure.configuration.json.Json;
import com.fullcycle.admin.catalog.infraestructure.genre.models.CreateGenreRequest;
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
import java.util.Map;
import java.util.function.Function;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
@Testcontainers
public class GenreE2ETest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private GenreRepository genreRepository;

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

    private CategoryID givenACategory(String name, String description, boolean isActive) throws Exception {
        final var requestBody = new CreateCategoryRequest(name, description, isActive);

        final var request = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(requestBody));

        final var response = mvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString();

        var responseId = Json.readValue(response, Map.class).get("id");

        return CategoryID.from((String) responseId);
    }

    private GenreID givenAGenre(final String name, final boolean isActive, final List<CategoryID> categoryIDS) throws Exception {
        final var requestBody = new CreateGenreRequest(name, mapTo(categoryIDS, CategoryID::getValue), isActive);

        final var request = post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(requestBody));

        final var response = mvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString();

        var responseId = Json.readValue(response, Map.class).get("id");

        return GenreID.from((String) responseId);
    }

    private <A, D> List<D> mapTo(final List<A> actual, final Function<A, D> mapper) {
        return actual.stream()
                .map(mapper)
                .toList();
    }
}
