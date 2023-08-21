package com.fullcycle.admin.catalog.infraestructure.genre.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record GenreResponse(
        String id,
        String name,
        @JsonProperty("is_active") boolean active,
        @JsonProperty("categories_id") List<String> categories,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("deleted_at") Instant deletedAt
) {}
