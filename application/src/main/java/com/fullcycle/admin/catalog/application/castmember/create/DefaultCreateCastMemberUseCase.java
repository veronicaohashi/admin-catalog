package com.fullcycle.admin.catalog.application.castmember.create;

import com.fullcycle.admin.catalog.domain.castmember.CastMember;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;

import java.util.Objects;

public final class DefaultCreateCastMemberUseCase extends CreateCastMemberUseCase{

    private final CastMemberGateway castMemberGateway;

    public DefaultCreateCastMemberUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public CreateCastMemberOutput execute(CreateCastMemberCommand createCastMemberCommand) {
        final var name = createCastMemberCommand.name();
        final var type = createCastMemberCommand.type();

        final var notification = Notification.create();
        final var member = notification.validate(() -> CastMember.newMember(name, type));

        if(notification.hasError()) {
            throw new NotificationException("Could not create Aggregate Cast Member", notification);
        }

        return CreateCastMemberOutput.from(castMemberGateway.create(member));
    }
}
