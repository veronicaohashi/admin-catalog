package com.fullcycle.admin.catalog.infraestructure.video.persistence;

import com.fullcycle.admin.catalog.domain.video.AudioVideoMedia;
import com.fullcycle.admin.catalog.domain.video.MediaStatus;

import javax.persistence.*;

@Table(name = "videos_video_media")
@Entity(name = "AudioVideoMedia")
public class AudioVideoMediaJpaEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "raw_location", nullable = false)
    private String rawLocation;

    @Column(name = "encoded_path", nullable = false)
    private String encodedPath;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaStatus status;

    public AudioVideoMediaJpaEntity() {}

    private AudioVideoMediaJpaEntity(
            final String id,
            final String name,
            final String rawLocation,
            final String encodedPath,
            final MediaStatus status
    ) {
        this.id = id;
        this.name = name;
        this.rawLocation = rawLocation;
        this.encodedPath = encodedPath;
        this.status = status;
    }

    public static AudioVideoMediaJpaEntity from(final AudioVideoMedia media) {
        return new AudioVideoMediaJpaEntity(
                media.checksum(),
                media.name(),
                media.rawLocation(),
                media.encodedPath(),
                media.status()
        );
    }

    public AudioVideoMedia toDomain() {
        return AudioVideoMedia.with(
                getId(),
                getName(),
                getRawLocation(),
                getEncodedPath(),
                getStatus()
        );
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRawLocation() {
        return rawLocation;
    }

    public String getEncodedPath() {
        return encodedPath;
    }

    public MediaStatus getStatus() {
        return status;
    }
}
