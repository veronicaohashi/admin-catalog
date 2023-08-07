package com.fullcycle.admin.catalog.e2e.category;

import com.fullcycle.admin.catalog.E2ETest;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.infraestructure.category.models.CategoryResponse;
import com.fullcycle.admin.catalog.infraestructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalog.infraestructure.category.models.UpdateCategoryRequest;
import com.fullcycle.admin.catalog.infraestructure.category.persistence.CategoryRepository;
import com.fullcycle.admin.catalog.infraestructure.configuration.json.Json;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
@Testcontainers
public class CategoryE2ETest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CategoryRepository categoryRepository;

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
    void asACatalogAdminIShouldBeAbleToCreateANewCategoryWithValidValues() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        Assertions.assertEquals(0, categoryRepository.count());
        final var categoryId = givenACategory(expectedName, expectedDescription, expectedIsActive);

        final var category = categoryRepository.findById(categoryId.getValue()).get();

        Assertions.assertEquals(expectedName, category.getName());
        Assertions.assertEquals(expectedDescription, category.getDescription());
        Assertions.assertEquals(expectedIsActive, category.isActive());
        Assertions.assertNotNull(category.getCreatedAt());
        Assertions.assertNotNull(category.getUpdatedAt());
        Assertions.assertNull(category.getDeletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToNavigateToAllCategories() throws Exception {
        Assertions.assertEquals(0, categoryRepository.count());
        givenACategory("Filmes", null, true);
        givenACategory("Documentários", null, true);
        givenACategory("Séries", null, true);

        listCategories(0, 2)
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(2)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Documentários")))
                .andExpect(jsonPath("$.items[1].name", equalTo("Filmes")));

        listCategories(1, 2)
                .andExpect(jsonPath("$.current_page", equalTo(1)))
                .andExpect(jsonPath("$.per_page", equalTo(2)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Séries")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSearchBetweenAllCategories() throws Exception {
        Assertions.assertEquals(0, categoryRepository.count());
        givenACategory("Filmes", null, true);
        givenACategory("Documentários", null, true);
        givenACategory("Séries", null, true);

        listCategories(0, 10, "fil")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(10)))
                .andExpect(jsonPath("$.total", equalTo(1)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Filmes")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSortAllCategoriesByDescriptionDesc() throws Exception {
        Assertions.assertEquals(0, categoryRepository.count());
        givenACategory("Filmes", "C", true);
        givenACategory("Documentários", "Z", true);
        givenACategory("Séries", "A", true);

        listCategories(0, 10, "", "description", "desc")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(10)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Documentários")))
                .andExpect(jsonPath("$.items[1].name", equalTo("Filmes")))
                .andExpect(jsonPath("$.items[2].name", equalTo("Séries")));

    }

    @Test
    void asACatalogAdminIShouldBeAbleToGetACategoryByItsIdentifier() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        Assertions.assertEquals(0, categoryRepository.count());
        final var categoryId = givenACategory(expectedName, expectedDescription, expectedIsActive);

        final var category = retrieveACategory(categoryId.getValue());

        Assertions.assertEquals(expectedName, category.name());
        Assertions.assertEquals(expectedDescription, category.description());
        Assertions.assertEquals(expectedIsActive, category.active());
        Assertions.assertNotNull(category.createdAt());
        Assertions.assertNotNull(category.updatedAt());
        Assertions.assertNull(category.deletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSeeATreatedErrorByGettingANotFoundCategory() throws Exception {
        final var request = get("/categories/123")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo("Category with ID 123 was not found")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToUpdateACategoryByItsIdentifier() throws Exception {
        final var expectedName = "Séries";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var categoryId = givenACategory("Filmes", "A categoria mais assistida", true);

        final var requestBody = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var request = put("/categories/" + categoryId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(requestBody));

        mvc.perform(request)
                .andExpect(status().isOk());

        final var category = categoryRepository.findById(categoryId.getValue()).get();

        Assertions.assertEquals(expectedName, category.getName());
        Assertions.assertEquals(expectedDescription, category.getDescription());
        Assertions.assertEquals(expectedIsActive, category.isActive());
        Assertions.assertNotNull(category.getCreatedAt());
        Assertions.assertNotNull(category.getUpdatedAt());
        Assertions.assertNull(category.getDeletedAt());

    }

    @Test
    void asACatalogAdminIShouldBeAbleToDeleteACategoryByItsIdentifier() throws Exception {
        final var categoryId = givenACategory("Filmes", "A categoria mais assistida", true);

        final var request = delete("/categories/" + categoryId.getValue())
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNoContent());

        Assertions.assertFalse(this.categoryRepository.existsById(categoryId.getValue()));
    }

    private ResultActions listCategories(final int page, final int perPage, final String search) throws Exception {
        return listCategories(page, perPage, search, "", "");
    }

    private ResultActions listCategories(final int page, final int perPage) throws Exception {
        return listCategories(page, perPage, "", "", "");
    }

    private ResultActions listCategories(
            final int page,
            final int perPage,
            final String search,
            final String sort,
            final String direction
    ) throws Exception {
        final var request = get("/categories")
                .queryParam("page", String.valueOf(page))
                .queryParam("perPage", String.valueOf(perPage))
                .queryParam("sort", sort)
                .queryParam("dir", direction)
                .queryParam("search", search)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        return mvc.perform(request);
    }

    private CategoryResponse retrieveACategory(String id) throws Exception {
        final var request = get("/categories/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var json = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        return Json.readValue(json, CategoryResponse.class);
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
}
