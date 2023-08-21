package com.fullcycle.admin.catalog.infraestructure.genre.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public record CreateGenreRequest(
        String name,
        @JsonProperty("categories_id") List<String> categories,
        @JsonProperty("is_active") boolean active
) {
    public List<String> categories() {
        return this.categories != null ? this.categories : Collections.emptyList();
    }
}
