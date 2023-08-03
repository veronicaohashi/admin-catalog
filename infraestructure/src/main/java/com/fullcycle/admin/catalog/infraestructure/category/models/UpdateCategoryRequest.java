package com.fullcycle.admin.catalog.infraestructure.category.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateCategoryRequest(
        String name,
        String description,
        @JsonProperty("is_active") boolean active
) {
}
