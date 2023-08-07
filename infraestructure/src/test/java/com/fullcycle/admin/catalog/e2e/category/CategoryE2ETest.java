package com.fullcycle.admin.catalog.e2e.category;

import com.fullcycle.admin.catalog.E2ETest;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.infraestructure.category.models.CategoryResponse;
import com.fullcycle.admin.catalog.infraestructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalog.infraestructure.category.persistence.CategoryRepository;
import com.fullcycle.admin.catalog.infraestructure.configuration.json.Json;
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

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        final var category = retrieveACategory(categoryId.getValue());

        Assertions.assertEquals(expectedName, category.name());
        Assertions.assertEquals(expectedDescription, category.description());
        Assertions.assertEquals(expectedIsActive, category.active());
        Assertions.assertNotNull(category.createdAt());
        Assertions.assertNotNull(category.updatedAt());
        Assertions.assertNull(category.deletedAt());
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
