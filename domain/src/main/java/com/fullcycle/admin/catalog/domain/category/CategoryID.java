package com.fullcycle.admin.catalog.domain.category;

import com.fullcycle.admin.catalog.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class CategoryID extends Identifier {
    private final String value;

    private CategoryID(final String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static CategoryID unique() {
        return CategoryID.from(UUID.randomUUID());
    }

    public static CategoryID from(final String id) {
        return new CategoryID(id);
    }

    public static CategoryID from(final UUID id) {
        return new CategoryID(id.toString().toLowerCase());
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CategoryID that = (CategoryID) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
