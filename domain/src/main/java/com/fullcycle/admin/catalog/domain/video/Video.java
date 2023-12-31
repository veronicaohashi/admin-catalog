package com.fullcycle.admin.catalog.domain.video;

import com.fullcycle.admin.catalog.domain.AggregateRoot;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.events.DomainEvent;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.utils.InstantUtils;
import com.fullcycle.admin.catalog.domain.validation.ValidationHandler;

import java.time.Instant;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;

public class Video extends AggregateRoot<VideoID> {
    private String title;
    private String description;
    private Year launchedAt;
    private double duration;
    private Rating rating;
    private boolean opened;
    private boolean published;
    private Instant createdAt;
    private Instant updatedAt;
    private ImageMedia banner;
    private ImageMedia thumbnail;
    private ImageMedia thumbnailHalf;
    private AudioVideoMedia trailer;
    private AudioVideoMedia video;
    private Set<CategoryID> categories;
    private Set<GenreID> genres;
    private Set<CastMemberID> castMembers;

    protected Video(
            final VideoID videoID,
            final String title,
            final String description,
            final Year launchedAt,
            final double duration,
            final Rating rating,
            final boolean opened,
            final boolean published,
            final Instant createdAt,
            final Instant updatedAt,
            final ImageMedia banner,
            final ImageMedia thumbnail,
            final ImageMedia thumbnailHalf,
            final AudioVideoMedia trailer,
            final AudioVideoMedia video,
            final Set<CategoryID> categories,
            final Set<GenreID> genres,
            final Set<CastMemberID> members,
            final List<DomainEvent> domainEvents
            ) {
        super(videoID, domainEvents);
        this.title = title;
        this.description = description;
        this.launchedAt = launchedAt;
        this.duration = duration;
        this.rating = rating;
        this.opened = opened;
        this.published = published;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.banner = banner;
        this.thumbnail = thumbnail;
        this.thumbnailHalf = thumbnailHalf;
        this.trailer = trailer;
        this.video = video;
        this.categories = categories;
        this.genres = genres;
        this.castMembers = members;
    }

    @Override
    public void validate(ValidationHandler handler) {
        new VideoValidator(this, handler).validate();
    }

    public static Video newVideo(
            final String title,
            final String description,
            final Year launchedAt,
            final double duration,
            final Rating rating,
            final boolean opened,
            final boolean published,
            final Set<CategoryID> categories,
            final Set<GenreID> genres,
            final Set<CastMemberID> members
    ) {
        final var now = InstantUtils.now();
        final var id = VideoID.unique();
        return new Video(
                id,
                title,
                description,
                launchedAt,
                duration,
                rating,
                opened,
                published,
                now,
                now,
                null,
                null,
                null,
                null,
                null,
                categories,
                genres,
                members,
                null
        );
    }

    public static Video with(final Video video) {
        return new Video(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getLaunchedAt(),
                video.getDuration(),
                video.getRating(),
                video.isOpened(),
                video.isPublished(),
                video.getCreatedAt(),
                video.getUpdatedAt(),
                video.getBanner().orElse(null),
                video.getThumbnail().orElse(null),
                video.getThumbnailHalf().orElse(null),
                video.getTrailer().orElse(null),
                video.getVideo().orElse(null),
                new HashSet<>(video.getCategories()),
                new HashSet<>(video.getGenres()),
                new HashSet<>(video.getCastMembers()),
                video.getDomainEvents()
        );
    }

    public static Video with(
            final VideoID videoID,
            final String title,
            final String description,
            final Year launchedAt,
            final double duration,
            final Rating rating,
            final boolean opened,
            final boolean published,
            final Instant createdAt,
            final Instant updatedAt,
            final ImageMedia banner,
            final ImageMedia thumbnail,
            final ImageMedia thumbnailHalf,
            final AudioVideoMedia trailer,
            final AudioVideoMedia video,
            final Set<CategoryID> categories,
            final Set<GenreID> genres,
            final Set<CastMemberID> members
    ) {
        return new Video(
                videoID,
                title,
                description,
                launchedAt,
                duration,
                rating,
                opened,
                published,
                createdAt,
                updatedAt,
                banner,
                thumbnail,
                thumbnailHalf,
                trailer,
                video,
                categories,
                genres,
                members,
                null
        );
    }

    public Video update(
            final String title,
            final String description,
            final Year launchedAt,
            final double duration,
            final Rating rating,
            final boolean opened,
            final boolean published,
            final Set<CategoryID> categories,
            final Set<GenreID> genres,
            final Set<CastMemberID> members
    ) {
        this.title = title;
        this.description = description;
        this.launchedAt = launchedAt;
        this.duration = duration;
        this.rating = rating;
        this.opened = opened;
        this.published = published;
        this.updatedAt = InstantUtils.now();
        this.setCategories(categories);
        this.setGenres(genres);
        this.setCastMembers(members);

        return this;
    }

    public Video processing(final VideoMediaType type) {
        if (VideoMediaType.VIDEO == type) {
            getVideo()
                    .ifPresent(media -> updateVideo(media.processing()));
        } else if (VideoMediaType.TRAILER == type) {
            getTrailer()
                    .ifPresent(media -> updateTrailer(media.processing()));
        }

        return this;
    }

    public Video completed(final VideoMediaType type, final String encodedPath) {
        if (VideoMediaType.VIDEO == type) {
            getVideo()
                    .ifPresent(media -> updateVideo(media.completed(encodedPath)));
        } else if (VideoMediaType.TRAILER == type) {
            getTrailer()
                    .ifPresent(media -> updateTrailer(media.completed(encodedPath)));
        }

        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Year getLaunchedAt() {
        return launchedAt;
    }

    public double getDuration() {
        return duration;
    }

    public Rating getRating() {
        return rating;
    }

    public boolean isOpened() {
        return opened;
    }

    public boolean isPublished() {
        return published;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Optional<ImageMedia> getBanner() {
        return Optional.ofNullable(banner);
    }

    public Optional<ImageMedia> getThumbnail() {
        return Optional.ofNullable(thumbnail);
    }

    public Optional<ImageMedia> getThumbnailHalf() {
        return Optional.ofNullable(thumbnailHalf);
    }

    public Optional<AudioVideoMedia> getTrailer() {
        return Optional.ofNullable(trailer);
    }

    public Optional<AudioVideoMedia> getVideo() {
        return Optional.ofNullable(video);
    }

    public Set<CategoryID> getCategories() {
        return categories != null ? unmodifiableSet(categories) : emptySet();
    }

    public Set<GenreID> getGenres() {
        return genres != null ? unmodifiableSet(genres) : emptySet();
    }

    public Set<CastMemberID> getCastMembers() {
        return castMembers != null ? unmodifiableSet(castMembers) : emptySet();
    }

    public Video updateBanner(final ImageMedia banner) {
        this.banner = banner;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Video updateThumbnail(final ImageMedia thumbnail) {
        this.thumbnail = thumbnail;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Video updateThumbnailHalf(final ImageMedia thumbnailHalf) {
        this.thumbnailHalf = thumbnailHalf;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Video updateTrailer(final AudioVideoMedia trailer) {
        this.trailer = trailer;
        this.updatedAt = InstantUtils.now();
        onAudioVideoMediaUpdated(trailer);
        return this;
    }

    public Video updateVideo(final AudioVideoMedia video) {
        this.video = video;
        this.updatedAt = InstantUtils.now();
        onAudioVideoMediaUpdated(video);
        return this;
    }

    private void onAudioVideoMediaUpdated(final AudioVideoMedia media) {
        if(media != null && media.isPendingEncode()) {
            registerEvent(new VideoMediaCreated(getId().getValue(), media.rawLocation()));
        }
    }

    private void setCategories(final Set<CategoryID> categories) {
        this.categories = categories != null ? new HashSet<>(categories) : emptySet();
    }

    private void setGenres(final Set<GenreID> genres) {
        this.genres = genres != null ? new HashSet<>(genres) : emptySet();
    }

    private void setCastMembers(final Set<CastMemberID> castMembers) {
        this.castMembers = castMembers != null ? new HashSet<>(castMembers) : emptySet();
    }
}
