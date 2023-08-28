package com.fullcycle.admin.catalog.e2e;

import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.infraestructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalog.infraestructure.configuration.json.Json;
import com.fullcycle.admin.catalog.infraestructure.genre.models.CreateGenreRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface MockDsl {

    MockMvc mvc();

    default CategoryID givenACategory(final String name, final String description, final boolean isActive) throws Exception {
        final var requestBody = new CreateCategoryRequest(name, description, isActive);
        var responseId = given("/categories", requestBody);
        return CategoryID.from(responseId);
    }

    default GenreID givenAGenre(final String name, final boolean isActive, final List<CategoryID> categoryIDS) throws Exception {
        final var requestBody = new CreateGenreRequest(name, mapTo(categoryIDS, CategoryID::getValue), isActive);
        var responseId = given("/genres", requestBody);
        return GenreID.from(responseId);
    }

    default <A, D> List<D> mapTo(final List<A> actual, final Function<A, D> mapper) {
        return actual.stream()
                .map(mapper)
                .toList();
    }

    private String given(final String url, final Object requestBody) throws Exception {
        final var request = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(requestBody));

        final var response = mvc().perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString();

        var responseId = Json.readValue(response, Map.class).get("id");

        return (String) responseId;
    }
}
