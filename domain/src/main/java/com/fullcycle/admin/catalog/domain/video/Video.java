package com.fullcycle.admin.catalog.domain.video;

import com.fullcycle.admin.catalog.domain.AggregateRoot;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.utils.InstantUtils;
import com.fullcycle.admin.catalog.domain.validation.ValidationHandler;

import java.time.Instant;
import java.time.Year;
import java.util.Collections;
import java.util.HashSet;
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
            final Set<CastMemberID> members
    ) {
        super(videoID);
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
                members
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
                new HashSet<>(video.getCastMembers())
        );
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Year getLaunchedAt() {
        return launchedAt;
    }

    public void setLaunchedAt(final Year launchedAt) {
        this.launchedAt = launchedAt;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(final double duration) {
        this.duration = duration;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(final Rating rating) {
        this.rating = rating;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(final boolean opened) {
        this.opened = opened;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(final boolean published) {
        this.published = published;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Optional<ImageMedia> getBanner() {
        return Optional.ofNullable(banner);
    }

    public void setBanner(final ImageMedia banner) {
        this.banner = banner;
    }

    public Optional<ImageMedia> getThumbnail() {
        return Optional.ofNullable(thumbnail);
    }

    public void setThumbnail(final ImageMedia thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Optional<ImageMedia> getThumbnailHalf() {
        return Optional.ofNullable(thumbnailHalf);
    }

    public void setThumbnailHalf(final ImageMedia thumbnailHalf) {
        this.thumbnailHalf = thumbnailHalf;
    }

    public Optional<AudioVideoMedia> getTrailer() {
        return Optional.ofNullable(trailer);
    }

    public void setTrailer(final AudioVideoMedia trailer) {
        this.trailer = trailer;
    }

    public Optional<AudioVideoMedia> getVideo() {
        return Optional.ofNullable(video);
    }

    public void setVideo(final AudioVideoMedia video) {
        this.video = video;
    }

    public Set<CategoryID> getCategories() {
        return categories != null ? unmodifiableSet(categories) : emptySet();
    }

    public void setCategories(final Set<CategoryID> categories) {
        this.categories = categories != null ? new HashSet<>(categories) : emptySet();
    }

    public Set<GenreID> getGenres() {
        return genres != null ? unmodifiableSet(genres) : emptySet();
    }

    public void setGenres(final Set<GenreID> genres) {
        this.genres = genres != null ? new HashSet<>(genres) : emptySet();
    }

    public Set<CastMemberID> getCastMembers() {
        return castMembers != null ? unmodifiableSet(castMembers) : emptySet();
    }

    public void setCastMembers(final Set<CastMemberID> castMembers) {
        this.castMembers = castMembers != null ? new HashSet<>(castMembers) : emptySet();
    }
}
