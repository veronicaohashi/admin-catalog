package com.fullcycle.admin.catalog.application.castmember.create;

import com.fullcycle.admin.catalog.domain.castmember.CastMember;

public record CreateCastMemberOutput(
        String id
) {
    public static CreateCastMemberOutput from(final CastMember member) {
        return new CreateCastMemberOutput(member.getId().getValue());
    }

    public static CreateCastMemberOutput from(final String id) {
        return new CreateCastMemberOutput(id);
    }
}
