package com.fullcycle.admin.catalog.application.castmember.update;

import com.fullcycle.admin.catalog.domain.Identifier;
import com.fullcycle.admin.catalog.domain.castmember.CastMember;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;

import java.util.Objects;
import java.util.function.Supplier;

public non-sealed class DefaultUpdateCastMemberUseCase extends UpdateCastMemberUseCase{

    private final CastMemberGateway castMemberGateway;

    public DefaultUpdateCastMemberUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public UpdateCastMemberOutput execute(UpdateCastMemberCommand updateCastMemberCommand) {
        final var id = CastMemberID.from(updateCastMemberCommand.id());
        final var name = updateCastMemberCommand.name();
        final var type = updateCastMemberCommand.type();

        final var member = castMemberGateway.findById(id)
                .orElseThrow(notFound(id));

        final var notification = Notification.create();
        notification.validate(() -> member.update(name, type));

        if(notification.hasError()) {
            throw new NotificationException(
                    "Could not update Aggregate CastMember %s".formatted(id.getValue()), notification
            );
        }

        return UpdateCastMemberOutput.from(castMemberGateway.update(member));
    }

    private static Supplier<NotFoundException> notFound(Identifier id) {
        return () -> NotFoundException.with(CastMember.class, id);
    }
}
