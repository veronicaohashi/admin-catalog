package com.fullcycle.admin.catalog.domain.video;

import com.fullcycle.admin.catalog.domain.ValueObject;
import com.fullcycle.admin.catalog.domain.utils.IdUtils;

import java.util.Objects;

import static com.fullcycle.admin.catalog.domain.video.MediaStatus.PENDING;

public class AudioVideoMedia extends ValueObject {
    private final String id;
    private final String checksum;
    private final String name;
    private final String rawLocation;
    private final String encodedPath;
    private final MediaStatus status;

    private AudioVideoMedia(
            final String id,
            final String checksum,
            final String name,
            final String rawLocation,
            final String encodedPath,
            final MediaStatus status
    ) {
        this.id = Objects.requireNonNull(id);
        this.checksum = Objects.requireNonNull(checksum);
        this.name = Objects.requireNonNull(name);
        this.rawLocation = Objects.requireNonNull(rawLocation);
        this.encodedPath = Objects.requireNonNull(encodedPath);
        this.status = Objects.requireNonNull(status);
    }

    public static AudioVideoMedia with(
            final String id,
            final String checksum,
            final String name,
            final String rawLocation,
            final String encodedLocation,
            final MediaStatus status
    ) {
        return new AudioVideoMedia(id, checksum, name, rawLocation, encodedLocation, status);
    }

    public static AudioVideoMedia with(
            final String checksum,
            final String name,
            final String rawLocation
    ) {
        return new AudioVideoMedia(IdUtils.uuid(), checksum, name, rawLocation, "", PENDING);
    }

    public AudioVideoMedia processing() {
        return AudioVideoMedia.with(
                id(),
                checksum(),
                name(),
                rawLocation(),
                encodedPath(),
                MediaStatus.PROCESSING
        );
    }

    public AudioVideoMedia completed(final String encodedPath) {
        return AudioVideoMedia.with(
                id(),
                checksum(),
                name(),
                rawLocation(),
                encodedPath,
                MediaStatus.COMPLETED
        );
    }

    public String id() {
        return id;
    }

    public String checksum() {
        return checksum;
    }

    public String name() {
        return name;
    }

    public String rawLocation() {
        return rawLocation;
    }

    public String encodedPath() {
        return encodedPath;
    }

    public MediaStatus status() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AudioVideoMedia that = (AudioVideoMedia) o;
        return Objects.equals(checksum, that.checksum) && Objects.equals(rawLocation, that.rawLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checksum, rawLocation);
    }

    public boolean isPendingEncode() { return status == PENDING; }
}
