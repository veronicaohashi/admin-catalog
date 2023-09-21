package com.fullcycle.admin.catalog.infraestructure.castmember.presenters;

import com.fullcycle.admin.catalog.application.castmember.retrieve.get.CastMemberOutput;
import com.fullcycle.admin.catalog.infraestructure.castmember.models.CastMemberResponse;

public interface CastMemberApiPresenter {
    static CastMemberResponse present(final CastMemberOutput output) {
        return new CastMemberResponse(
                output.id(),
                output.name(),
                output.type().name(),
                output.createdAt(),
                output.updatedAt()
        );
    }
}
