package com.fullcycle.admin.catalog.infraestructure.api.controllers;

import com.fullcycle.admin.catalog.application.castmember.create.CreateCastMemberCommand;
import com.fullcycle.admin.catalog.application.castmember.create.CreateCastMemberUseCase;
import com.fullcycle.admin.catalog.application.castmember.retrieve.get.GetCastMemberByIdUseCase;
import com.fullcycle.admin.catalog.application.castmember.update.UpdateCastMemberCommand;
import com.fullcycle.admin.catalog.application.castmember.update.UpdateCastMemberUseCase;
import com.fullcycle.admin.catalog.infraestructure.api.CastMemberAPI;
import com.fullcycle.admin.catalog.infraestructure.castmember.models.CastMemberResponse;
import com.fullcycle.admin.catalog.infraestructure.castmember.models.CreateCastMemberRequest;
import com.fullcycle.admin.catalog.infraestructure.castmember.models.UpdateCastMemberRequest;
import com.fullcycle.admin.catalog.infraestructure.castmember.presenters.CastMemberApiPresenter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class CastMemberController implements CastMemberAPI {

    private final CreateCastMemberUseCase createCastMemberUseCase;
    private final GetCastMemberByIdUseCase getCastMemberByIdUseCase;
    private final UpdateCastMemberUseCase updateCastMemberUseCase;

    public CastMemberController(
            final CreateCastMemberUseCase createCastMemberUseCase,
            final GetCastMemberByIdUseCase getCastMemberByIdUseCase,
            final UpdateCastMemberUseCase updateCastMemberUseCase
    ) {
        this.createCastMemberUseCase = Objects.requireNonNull(createCastMemberUseCase);
        this.getCastMemberByIdUseCase = Objects.requireNonNull(getCastMemberByIdUseCase);
        this.updateCastMemberUseCase = Objects.requireNonNull(updateCastMemberUseCase);
    }

    @Override
    public ResponseEntity<?> create(final CreateCastMemberRequest input) {
        final var command = CreateCastMemberCommand.with(input.name(), input.type());

        final var output = createCastMemberUseCase.execute(command);

        return ResponseEntity.created(URI.create("/cast_members/" + output.id())).body(output);
    }

    @Override
    public CastMemberResponse getById(final String id) {
        final var output = getCastMemberByIdUseCase.execute(id);
        return CastMemberApiPresenter.present(output);
    }

    @Override
    public ResponseEntity<?> update(final String id, final UpdateCastMemberRequest input) {
        final var command = UpdateCastMemberCommand.with(id, input.name(), input.type());
        return ResponseEntity.ok(updateCastMemberUseCase.execute(command));
    }
}
