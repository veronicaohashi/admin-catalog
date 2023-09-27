package com.fullcycle.admin.catalog.domain.video;

import com.fullcycle.admin.catalog.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class VideoID extends Identifier {
    private final String value;

    private VideoID(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static VideoID unique() {
        return VideoID.from(UUID.randomUUID());
    }

    public static VideoID from(final UUID id) {
        return new VideoID(id.toString().toLowerCase());
    }

    public static VideoID from(final String id) {
        return new VideoID(id);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final VideoID that = (VideoID) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
