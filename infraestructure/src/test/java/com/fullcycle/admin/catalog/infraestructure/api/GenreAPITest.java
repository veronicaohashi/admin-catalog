package com.fullcycle.admin.catalog.infraestructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalog.ControllerTest;
import com.fullcycle.admin.catalog.application.genre.create.CreateGenreOutput;
import com.fullcycle.admin.catalog.application.genre.create.CreateGenreUseCase;
import com.fullcycle.admin.catalog.application.genre.delete.DeleteGenreUseCase;
import com.fullcycle.admin.catalog.application.genre.retrieve.get.GenreOutput;
import com.fullcycle.admin.catalog.application.genre.retrieve.get.GetGenreByIdUseCase;
import com.fullcycle.admin.catalog.application.genre.retrieve.list.GenreListOutput;
import com.fullcycle.admin.catalog.application.genre.retrieve.list.ListGenreUseCase;
import com.fullcycle.admin.catalog.application.genre.update.UpdateGenreOutput;
import com.fullcycle.admin.catalog.application.genre.update.UpdateGenreUseCase;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import com.fullcycle.admin.catalog.infraestructure.genre.models.CreateGenreRequest;
import com.fullcycle.admin.catalog.infraestructure.genre.models.UpdateGenreRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ControllerTest(controllers = GenreAPI.class)
class GenreAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateGenreUseCase createGenreUseCase;

    @MockBean
    private GetGenreByIdUseCase getGenreByIdUseCase;

    @MockBean
    private UpdateGenreUseCase updateGenreUseCase;

    @MockBean
    private DeleteGenreUseCase deleteGenreUseCase;

    @MockBean
    private ListGenreUseCase listGenreUseCase;

    @Test
    void givenAValidCommand_whenCallsCreateGenre_thenReturnGenreId() throws Exception {
        final var expectedName = "Terror";
        final var expectedIsActive = true;
        final var expectedCategories = List.of("123", "456");
        final var expectedId = "123";
        final var input = new CreateGenreRequest(expectedName, expectedCategories, expectedIsActive);

        when(createGenreUseCase.execute(any()))
                .thenReturn(CreateGenreOutput.from(expectedId));

        final var request = post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input));
        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/genres/123"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", Matchers.equalTo(expectedId)));

        verify(createGenreUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedCategories, cmd.categories())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    void givenAInvalidName_whenCallsCreateGenre_thenReturnNotification() throws Exception {
        final var expectedErrorMessage = "'name' should not be null";
        final var input = new CreateGenreRequest(null, List.of(), true);

        when(createGenreUseCase.execute(any()))
                .thenThrow(new NotificationException("Error", Notification.create(
                        new Error(expectedErrorMessage)
                )));

        final var request = post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input));
        mvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", Matchers.nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0].message", Matchers.equalTo(expectedErrorMessage)));
    }

    @Test
    void givenAValidId_whenCallsGetGenreById_thenReturnGenre() throws Exception {
        final var expectedName = "Terror";
        final var expectedIsActive = false;
        final var expectedCategories = List.of("123", "456");
        final var genre = Genre.newGenre(expectedName, expectedIsActive).addCategories(
                expectedCategories.stream()
                        .map(CategoryID::from)
                        .toList()
        );
        final var expectedId = genre.getId().getValue();

        when(getGenreByIdUseCase.execute(any()))
                .thenReturn(GenreOutput.from(genre));

        final var request = get("/genres/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", Matchers.equalTo(expectedId)))
                .andExpect(jsonPath("$.name", Matchers.equalTo(expectedName)))
                .andExpect(jsonPath("$.is_active", Matchers.equalTo(expectedIsActive)))
                .andExpect(jsonPath("$.categories_id", Matchers.equalTo(expectedCategories)))
                .andExpect(jsonPath("$.created_at", Matchers.equalTo(genre.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updated_at", Matchers.equalTo(genre.getUpdatedAt().toString())))
                .andExpect(jsonPath("$.deleted_at", Matchers.equalTo(genre.getDeletedAt().toString())));
        verify(getGenreByIdUseCase).execute(eq(expectedId));
    }

    @Test
    void givenAnInvalidId_whenCallsGetGenreById_thenReturnNotFound() throws Exception {
        final var expectedErrorMessage = "Genre with ID 123 was not found";
        final var invalidId = "123";
        when(getGenreByIdUseCase.execute(any()))
                .thenThrow(NotFoundException.with(Genre.class, GenreID.from(invalidId)));

        final var request = get("/genres/{id}", invalidId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", Matchers.equalTo(expectedErrorMessage)));

        verify(getGenreByIdUseCase).execute(eq(invalidId));
    }

    @Test
    void givenAValidCommand_whenCallsUpdateGenre_thenReturnGenreId() throws Exception {
        final var expectedName = "Terror";
        final var expectedIsActive = false;
        final var expectedCategories = List.of("123", "456");
        final var genre = Genre.newGenre("Ação", true);
        final var expectedId = genre.getId().getValue();
        final var input = new UpdateGenreRequest(expectedName, expectedCategories, expectedIsActive);
        when(updateGenreUseCase.execute(any()))
                .thenReturn(UpdateGenreOutput.from(genre));

        final var request = put("/genres/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input));
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", Matchers.equalTo(expectedId)));

        verify(updateGenreUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedCategories, cmd.categories())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    void givenAnInvalidName_whenCallsUpdateGenre_thenReturnNotification() throws Exception {
        final var expectedErrorMessage = "'name' should not be null";
        final var input = new UpdateGenreRequest(null, List.of(), true);
        when(updateGenreUseCase.execute(any()))
                .thenThrow(new NotificationException("Error", Notification.create(new Error(expectedErrorMessage))));

        final var request = put("/genres/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input));
        mvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0].message", Matchers.equalTo(expectedErrorMessage)));
    }

    @Test
    void givenAValidId_whenCallsDeleteGenre_thenReturnOK() throws Exception {
        doNothing().when(deleteGenreUseCase).execute(any());

        final var request = delete("/genres/{id}", "123")
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(request)
                .andExpect(status().isNoContent());

        verify(deleteGenreUseCase).execute(eq("123"));
    }

    @Test
    void givenValidParams_whenCallsListGenres_thenReturnGenres() throws Exception {
        final var genre = Genre.newGenre("Terror", false);
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 1;
        final var expectedTotal = 1;
        final var expectedItems = List.of(GenreListOutput.from(genre));
        when(listGenreUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        final var request = get("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("direction", expectedDirection)
                .queryParam("search", expectedTerms);
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", Matchers.equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", Matchers.equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", Matchers.equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items[0].id", Matchers.equalTo(genre.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", Matchers.equalTo(genre.getName())))
                .andExpect(jsonPath("$.items[0].is_active", Matchers.equalTo(genre.isActive())))
                .andExpect(jsonPath("$.items[0].created_at", Matchers.equalTo(genre.getCreatedAt().toString())))
                .andExpect(jsonPath("$.items[0].deleted_at", Matchers.equalTo(genre.getDeletedAt().toString())));

        verify(listGenreUseCase).execute(argThat(query ->
                Objects.equals(expectedPage, query.page())
                        && Objects.equals(expectedPerPage, query.perPage())
                        && Objects.equals(expectedDirection, query.direction())
                        && Objects.equals(expectedSort, query.sort())
                        && Objects.equals(expectedTerms, query.terms())
        ));
    }
}
