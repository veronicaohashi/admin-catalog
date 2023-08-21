package com.fullcycle.admin.catalog.infraestructure.genre.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record UpdateGenreRequest(
        String name,
        @JsonProperty("categories_id") List<String> categories,
        @JsonProperty("is_active") boolean active

){}
