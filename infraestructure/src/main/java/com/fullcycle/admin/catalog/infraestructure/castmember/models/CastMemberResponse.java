package com.fullcycle.admin.catalog.infraestructure.castmember.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record CastMemberResponse(
        String id,
        String name,
        String type,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("updated_at") String updatedAt
) {
}
