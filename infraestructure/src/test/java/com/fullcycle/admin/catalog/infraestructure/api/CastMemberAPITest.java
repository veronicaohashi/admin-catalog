package com.fullcycle.admin.catalog.infraestructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalog.ControllerTest;
import com.fullcycle.admin.catalog.domain.Fixture;
import com.fullcycle.admin.catalog.application.castmember.create.CreateCastMemberOutput;
import com.fullcycle.admin.catalog.application.castmember.create.DefaultCreateCastMemberUseCase;
import com.fullcycle.admin.catalog.application.castmember.delete.DefaultDeleteCastMemberUseCase;
import com.fullcycle.admin.catalog.application.castmember.retrieve.get.CastMemberOutput;
import com.fullcycle.admin.catalog.application.castmember.retrieve.get.DefaultGetCastMemberByIdUseCase;
import com.fullcycle.admin.catalog.application.castmember.retrieve.list.CastMemberListOutput;
import com.fullcycle.admin.catalog.application.castmember.retrieve.list.DefaultListCastMembersUseCase;
import com.fullcycle.admin.catalog.application.castmember.update.DefaultUpdateCastMemberUseCase;
import com.fullcycle.admin.catalog.application.castmember.update.UpdateCastMemberOutput;
import com.fullcycle.admin.catalog.domain.castmember.CastMember;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import com.fullcycle.admin.catalog.infraestructure.castmember.models.CreateCastMemberRequest;
import com.fullcycle.admin.catalog.infraestructure.castmember.models.UpdateCastMemberRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = CastMemberAPI.class)
class CastMemberAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private DefaultCreateCastMemberUseCase createCastMemberUseCase;

    @MockBean
    private DefaultGetCastMemberByIdUseCase getCastMemberByIdUseCase;

    @MockBean
    private DefaultUpdateCastMemberUseCase updateCastMemberUseCase;

    @MockBean
    private DefaultDeleteCastMemberUseCase deleteCastMemberUseCase;

    @MockBean
    private DefaultListCastMembersUseCase listCastMembersUseCase;

    @Test
    void givenAValidCommand_whenCallsCreateCastMember_thenReturnItsIdentifier() throws Exception {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();
        final var expectedId = "123";
        final var input = new CreateCastMemberRequest(expectedName, expectedType);
        when(createCastMemberUseCase.execute(any()))
                .thenReturn(CreateCastMemberOutput.from(expectedId));

        final var request = post("/cast_members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input));

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/cast_members/123"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(createCastMemberUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedType, cmd.type())
        ));
    }

    @Test
    void givenAnInvalidName_whenCallsCreateCastMember_thenReturnNotification() throws Exception {
        final var expectedErrorMessage = "'name' should not be null";
        final var input = new CreateCastMemberRequest(null, null);

        when(createCastMemberUseCase.execute(any()))
                .thenThrow(new NotificationException("Error", Notification.create(
                        new Error(expectedErrorMessage)
                )));

        final var request = post("/cast_members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input));

        mvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", Matchers.nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
    }

    @Test
    void givenAValidId_whenCallsGetById_thenReturnIt() throws Exception {
        final var expectedMember = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var expectedId = expectedMember.getId().getValue();
        when(getCastMemberByIdUseCase.execute(any()))
                .thenReturn(CastMemberOutput.from(expectedMember));

        final var request = get("/cast_members/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)))
                .andExpect(jsonPath("$.name", equalTo(expectedMember.getName())))
                .andExpect(jsonPath("$.type", equalTo(expectedMember.getType().name())))
                .andExpect(jsonPath("$.created_at", equalTo(expectedMember.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(expectedMember.getUpdatedAt().toString())));
        verify(getCastMemberByIdUseCase).execute(eq(expectedId));
    }

    @Test
    void givenAInvalidId_whenCallsGetById_thenReturnNotFound() throws Exception {
        final var expectedErrorMessage = "CastMember with ID 123 was not found";
        final var invalidId = "123";
        when(getCastMemberByIdUseCase.execute(any()))
                .thenThrow(NotFoundException.with(CastMember.class, CastMemberID.from(invalidId)));

        final var request = get("/cast_members/{id}", invalidId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
        verify(getCastMemberByIdUseCase).execute(eq(invalidId));
    }

    @Test
    void givenAValidCommand_whenCallsUpdateCastMember_thenReturnItsIdentifier() throws Exception {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var expectedId = member.getId().getValue();
        final var input = new UpdateCastMemberRequest(expectedName, expectedType);
        Mockito.when(updateCastMemberUseCase.execute(any()))
                .thenReturn(UpdateCastMemberOutput.from(member));

        final var request = put("/cast_members/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input));

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(updateCastMemberUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedType, cmd.type())
        ));
    }

    @Test
    void givenAnInvalidName_whenCallsUpdateCastMember_thenReturnNotification() throws Exception {
        final var expectedErrorMessage = "'name' should not be null";
        final var input = new UpdateCastMemberRequest(null, Fixture.CastMembers.type());
        when(updateCastMemberUseCase.execute(any()))
                .thenThrow(new NotificationException("Error", Notification.create(new Error(expectedErrorMessage))));

        final var request = put("/cast_members/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input));

        mvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0].message", Matchers.equalTo(expectedErrorMessage)));

        verify(updateCastMemberUseCase).execute(argThat(cmd ->
                Objects.equals(input.name(), cmd.name())
                        && Objects.equals(input.type(), cmd.type())
        ));
    }

    @Test
    void givenAnInvalidId_whenCallsUpdateCastMember_thenReturnNotFound() throws Exception {
        final var expectedErrorMessage = "CastMember with ID 123 was not found";
        final var invalidId = "123";
        final var input = new UpdateCastMemberRequest(Fixture.name(), Fixture.CastMembers.type());
        when(updateCastMemberUseCase.execute(any()))
                .thenThrow(NotFoundException.with(CastMember.class, CastMemberID.from(invalidId)));

        final var request = put("/cast_members/{id}", invalidId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input));

        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(updateCastMemberUseCase).execute(argThat(cmd ->
                Objects.equals(invalidId, cmd.id()) &&
                Objects.equals(input.name(), cmd.name()) &&
                Objects.equals(input.type(), cmd.type())
        ));
    }

    @Test
    void givenAValidId_whenCallsDeleteById_thenDeleteIt() throws Exception {
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var expectedId = member.getId().getValue();
        doNothing().when(deleteCastMemberUseCase).execute(expectedId);

        final var request = delete("/cast_members/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNoContent());

        verify(deleteCastMemberUseCase).execute(eq(expectedId));
    }

    @Test
    void givenValidParams_whenCallListCastMembers_thenReturnIt() throws Exception {
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTotal = 1;
        final var expectedTerms = "Alg";
        final var expectedSort = "type";
        final var expectedDirection = "desc";
        final var expectedItems = List.of(CastMemberListOutput.from(member));
        when(listCastMembersUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        final var request = get("/cast_members")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .queryParam("search", expectedTerms);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", Matchers.equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", Matchers.equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", Matchers.equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items[0].id", Matchers.equalTo(member.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", Matchers.equalTo(member.getName())))
                .andExpect(jsonPath("$.items[0].type", Matchers.equalTo(member.getType().name())))
                .andExpect(jsonPath("$.items[0].created_at", Matchers.equalTo(member.getCreatedAt().toString())));

        verify(listCastMembersUseCase).execute(argThat(query ->
                Objects.equals(expectedPage, query.page())
                        && Objects.equals(expectedPerPage, query.perPage())
                        && Objects.equals(expectedDirection, query.direction())
                        && Objects.equals(expectedSort, query.sort())
                        && Objects.equals(expectedTerms, query.terms())
        ));
    }
}