package com.fullcycle.admin.catalog.infraestructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalog.ControllerTest;
import com.fullcycle.admin.catalog.application.genre.create.CreateGenreCommand;
import com.fullcycle.admin.catalog.application.genre.create.CreateGenreOutput;
import com.fullcycle.admin.catalog.application.genre.create.CreateGenreUseCase;
import com.fullcycle.admin.catalog.application.genre.retrieve.get.GenreOutput;
import com.fullcycle.admin.catalog.application.genre.retrieve.get.GetGenreByIdUseCase;
import com.fullcycle.admin.catalog.domain.Identifier;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import com.fullcycle.admin.catalog.infraestructure.genre.models.CreateGenreRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
