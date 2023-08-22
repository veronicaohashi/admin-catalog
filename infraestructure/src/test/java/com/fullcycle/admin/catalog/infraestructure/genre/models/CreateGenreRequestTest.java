package com.fullcycle.admin.catalog.infraestructure.genre.models;

import com.fullcycle.admin.catalog.JacksonTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.util.List;

@JacksonTest
class CreateGenreRequestTest {
    @Autowired
    private JacksonTester<CreateGenreRequest> json;

    @Test
    void testUnmarshall() throws Exception {
        final var expectedName = "Terror";
        final var expectedIsActive = false;
        final var expectedCategories = "123";

        final var json = """
                {
                  "name": "%s",
                  "categories_id": ["%s"],
                  "is_active": %s
                }    
                """.formatted(expectedName, expectedCategories, expectedIsActive);

        final var actualJson = this.json.parse(json);

        Assertions.assertThat(actualJson)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("categories", List.of(expectedCategories))
                .hasFieldOrPropertyWithValue("active", expectedIsActive);
    }
}