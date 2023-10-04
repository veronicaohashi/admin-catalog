package com.fullcycle.admin.catalog.infraestructure.video.persistence;

import com.fullcycle.admin.catalog.domain.video.Rating;
import com.fullcycle.admin.catalog.domain.video.Video;
import com.fullcycle.admin.catalog.domain.video.VideoID;

import javax.persistence.*;
import java.time.Instant;
import java.time.Year;
import java.util.Optional;
import java.util.UUID;

@Table(name = "videos")
@Entity(name = "Video")
public class VideoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "year_launched", nullable = false)
    private int yearLaunched;

    @Column(name = "opened", nullable = false)
    private boolean opened;

    @Column(name = "published", nullable = false)
    private boolean published;

    @Enumerated(EnumType.STRING)
    private Rating rating;

    @Column(name = "duration", scale = 2)
    private double duration;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "video_id")
    private AudioVideoMediaJpaEntity video;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "trailer_id")
    private AudioVideoMediaJpaEntity trailer;


    private VideoJpaEntity(
            final UUID id,
            final String title,
            final String description,
            final int yearLaunched,
            final boolean opened,
            final boolean published,
            final Rating rating,
            final double duration,
            final Instant createdAt,
            final Instant updatedAt,
            final AudioVideoMediaJpaEntity video,
            final AudioVideoMediaJpaEntity trailer
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.yearLaunched = yearLaunched;
        this.opened = opened;
        this.published = published;
        this.rating = rating;
        this.duration = duration;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.video = video;
        this.trailer = trailer;
    }

    public static VideoJpaEntity from(final Video video) {
        return new VideoJpaEntity(
                UUID.fromString(video.getId().getValue()),
                video.getTitle(),
                video.getDescription(),
                video.getLaunchedAt().getValue(),
                video.isOpened(),
                video.isPublished(),
                video.getRating(),
                video.getDuration(),
                video.getCreatedAt(),
                video.getUpdatedAt(),
                video.getVideo()
                        .map(AudioVideoMediaJpaEntity::from)
                        .orElse(null),
                video.getTrailer()
                        .map(AudioVideoMediaJpaEntity::from)
                        .orElse(null)
        );
    }

    public Video toAggregate() {
        return Video.with(
                VideoID.from(getId()),
                getTitle(),
                getDescription(),
                Year.of(getYearLaunched()),
                getDuration(),
                getRating(),
                isOpened(),
                isPublished(),
                getCreatedAt(),
                getUpdatedAt(),
                null,
                null,
                null,
                Optional.ofNullable(getTrailer())
                        .map(AudioVideoMediaJpaEntity::toDomain)
                        .orElse(null),
                Optional.ofNullable(getVideo())
                        .map(AudioVideoMediaJpaEntity::toDomain)
                        .orElse(null),
                null,
                null,
                null
                );
    }

    public AudioVideoMediaJpaEntity getVideo() {
        return video;
    }

    public AudioVideoMediaJpaEntity getTrailer() {
        return trailer;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getYearLaunched() {
        return yearLaunched;
    }

    public boolean isOpened() {
        return opened;
    }

    public boolean isPublished() {
        return published;
    }

    public Rating getRating() {
        return rating;
    }

    public double getDuration() {
        return duration;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
