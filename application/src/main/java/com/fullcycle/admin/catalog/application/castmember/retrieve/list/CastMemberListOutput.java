package com.fullcycle.admin.catalog.application.castmember.retrieve.list;

import com.fullcycle.admin.catalog.domain.castmember.CastMember;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberType;

import java.time.Instant;

public record CastMemberListOutput(
        String id,
        String name,
        CastMemberType type,
        Instant createdAt
) {

    public static CastMemberListOutput from(final CastMember member) {
        return new CastMemberListOutput(
                member.getId().getValue(),
                member.getName(),
                member.getType(),
                member.getCreatedAt()
        );
    }
}
