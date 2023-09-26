package com.fullcycle.admin.catalog.e2e;

import com.fullcycle.admin.catalog.domain.Identifier;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.infraestructure.castmember.models.CreateCastMemberRequest;
import com.fullcycle.admin.catalog.infraestructure.category.models.CategoryResponse;
import com.fullcycle.admin.catalog.infraestructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalog.infraestructure.category.models.UpdateCategoryRequest;
import com.fullcycle.admin.catalog.infraestructure.configuration.json.Json;
import com.fullcycle.admin.catalog.infraestructure.genre.models.CreateGenreRequest;
import com.fullcycle.admin.catalog.infraestructure.genre.models.GenreResponse;
import com.fullcycle.admin.catalog.infraestructure.genre.models.UpdateGenreRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    default CastMemberID givenACastMember(final String name, final CastMemberType type) throws Exception {
        final var requestBody = new CreateCastMemberRequest(name, type);
        var responseId = given("/cast_members", requestBody);
        return CastMemberID.from(responseId);
    }

    default <A, D> List<D> mapTo(final List<A> actual, final Function<A, D> mapper) {
        return actual.stream()
                .map(mapper)
                .toList();
    }

    default ResultActions updateACategory(final Identifier id, final UpdateCategoryRequest request) throws Exception {
        return this.update("/categories/", id, request);
    }

    default ResultActions updateAGenre(final Identifier id, final UpdateGenreRequest request) throws Exception {
        return this.update("/genres/", id, request);
    }

    default ResultActions deleteAGenre(final Identifier id) throws Exception {
        return this.delete("/genres/", id);
    }

    default ResultActions deleteACategory(final Identifier id) throws Exception {
        return this.delete("/categories/", id);
    }

    default ResultActions listCategories(final int page, final int perPage, final String search) throws Exception {
        return listCategories(page, perPage, search, "", "");
    }

    default ResultActions listCategories(final int page, final int perPage) throws Exception {
        return listCategories(page, perPage, "", "", "");
    }

    default ResultActions listCategories(final int page, final int perPage, final String search, final String sort, final String direction) throws Exception {
        return this.list("/categories", page, perPage, search, sort, direction);
    }

    default ResultActions listGenres(final int page, final int perPage, final String search) throws Exception {
        return listGenres(page, perPage, search, "", "");
    }

    default ResultActions listGenres(final int page, final int perPage) throws Exception {
        return listGenres(page, perPage, "", "", "");
    }

    default ResultActions listGenres(final int page, final int perPage, final String search, final String sort, final String direction) throws Exception {
        return this.list("/genres", page, perPage, search, sort, direction);
    }

    default CategoryResponse retrieveACategory(final Identifier id) throws Exception {
        return retrieve("/categories/", id, CategoryResponse.class);
    }

    default GenreResponse retrieveAGenre(final Identifier id) throws Exception {
        return retrieve("/genres/", id, GenreResponse.class);
    }

    private <T> T retrieve(final String url, final Identifier id, final Class<T> clazz) throws Exception {
        final var request = get(url + id.getValue())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8);

        final var json = mvc().perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        return Json.readValue(json, clazz);
    }

    private ResultActions list(
            final String url,
            final int page,
            final int perPage,
            final String search,
            final String sort,
            final String direction
    ) throws Exception {
        final var request = get(url)
                .queryParam("page", String.valueOf(page))
                .queryParam("perPage", String.valueOf(perPage))
                .queryParam("sort", sort)
                .queryParam("dir", direction)
                .queryParam("search", search)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        return mvc().perform(request);
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

    private ResultActions delete(final String url, final Identifier id) throws Exception {
        final var request = MockMvcRequestBuilders.delete(url + id.getValue())
                .contentType(MediaType.APPLICATION_JSON);
        return mvc().perform(request);
    }

    private ResultActions update(final String url, final Identifier id, final Object requestBody) throws Exception {
        final var aRequest = put(url + id.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(requestBody));

        return this.mvc().perform(aRequest);
    }
}
