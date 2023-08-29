package com.fullcycle.admin.catalog.application.castmember.update;

import com.fullcycle.admin.catalog.domain.castmember.CastMember;

public record UpdateCastMemberOutput(
        String id
) {

    public static UpdateCastMemberOutput from(final CastMember member) {
        return new UpdateCastMemberOutput(member.getId().getValue());
    }
}
