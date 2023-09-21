package com.fullcycle.admin.catalog.infraestructure.castmember.models;

import com.fullcycle.admin.catalog.domain.castmember.CastMemberType;

public record CreateCastMemberRequest(
        String name,
        CastMemberType type
) {
}
