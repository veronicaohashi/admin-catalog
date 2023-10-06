package com.fullcycle.admin.catalog.domain.genre;

import com.fullcycle.admin.catalog.domain.Identifier;
import com.fullcycle.admin.catalog.domain.utils.IdUtils;

import java.util.Objects;

public class GenreID extends Identifier {
    private final String value;

    private GenreID(final String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static GenreID unique() {
        return GenreID.from(IdUtils.uuid());
    }

    public static GenreID from(final String id) {
        return new GenreID(id);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GenreID that = (GenreID) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
