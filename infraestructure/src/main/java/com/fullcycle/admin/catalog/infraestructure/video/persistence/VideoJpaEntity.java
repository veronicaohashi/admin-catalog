package com.fullcycle.admin.catalog.infraestructure.video.persistence;

import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.utils.CollectionUtils;
import com.fullcycle.admin.catalog.domain.video.Rating;
import com.fullcycle.admin.catalog.domain.video.Video;
import com.fullcycle.admin.catalog.domain.video.VideoID;

import javax.persistence.*;
import java.time.Instant;
import java.time.Year;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Table(name = "videos")
@Entity(name = "Video")
public class VideoJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "banner_id")
    private ImageMediaJpaEntity banner;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "thumbnail_id")
    private ImageMediaJpaEntity thumbnail;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "thumbnail_half_id")
    private ImageMediaJpaEntity thumbnailHalf;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoCategoryJpaEntity> categories;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoGenreJpaEntity> genres;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoCastMemberJpaEntity> castMembers;

    public VideoJpaEntity() {
        this.categories = new HashSet<>(3);
        this.genres = new HashSet<>(3);
        this.castMembers = new HashSet<>(3);
    }

    private VideoJpaEntity(
            final String id,
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
            final AudioVideoMediaJpaEntity trailer,
            final ImageMediaJpaEntity banner,
            final ImageMediaJpaEntity thumbnail,
            final ImageMediaJpaEntity thumbnailHalf
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
        this.banner = banner;
        this.thumbnail = thumbnail;
        this.thumbnailHalf = thumbnailHalf;
        this.categories = new HashSet<>(3);
        this.genres = new HashSet<>(3);
        this.castMembers = new HashSet<>(3);
    }

    public static VideoJpaEntity from(final Video video) {
        final var entity = new VideoJpaEntity(
                video.getId().getValue(),
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
                        .orElse(null),
                video.getBanner()
                        .map(ImageMediaJpaEntity::from)
                        .orElse(null),
                video.getThumbnail()
                        .map(ImageMediaJpaEntity::from)
                        .orElse(null),
                video.getThumbnailHalf()
                        .map(ImageMediaJpaEntity::from)
                        .orElse(null)
        );


        video.getCategories()
                .forEach(entity::addCategory);

        video.getGenres()
                .forEach(entity::addGenre);

        video.getCastMembers()
                .forEach(entity::addCastMember);

        return entity;
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
                Optional.ofNullable(getBanner())
                        .map(ImageMediaJpaEntity::toDomain)
                        .orElse(null),
                Optional.ofNullable(getThumbnail())
                        .map(ImageMediaJpaEntity::toDomain)
                        .orElse(null),
                Optional.ofNullable(getThumbnailHalf())
                        .map(ImageMediaJpaEntity::toDomain)
                        .orElse(null),
                Optional.ofNullable(getTrailer())
                        .map(AudioVideoMediaJpaEntity::toDomain)
                        .orElse(null),
                Optional.ofNullable(getVideo())
                        .map(AudioVideoMediaJpaEntity::toDomain)
                        .orElse(null),
                getCategories().stream()
                        .map(it -> CategoryID.from(it.getId().getCategoryId()))
                        .collect(Collectors.toSet()),
                getGenres().stream()
                        .map(it -> GenreID.from(it.getId().getGenreId()))
                        .collect(Collectors.toSet()),
                getCastMembers().stream()
                        .map(it -> CastMemberID.from(it.getId().getCastMemberId()))
                        .collect(Collectors.toSet())
        );
    }

    public void addCategory(final CategoryID categoryID) {
        this.categories.add(VideoCategoryJpaEntity.from(this, categoryID));
    }

    public void addGenre(final GenreID genreID) {
        this.genres.add(VideoGenreJpaEntity.from(this, genreID));
    }

    public void addCastMember(final CastMemberID castMemberID) {
        this.castMembers.add(VideoCastMemberJpaEntity.from(this, castMemberID));
    }

    public Set<CategoryID> getCategoriesID() {
        return CollectionUtils.mapTo(getCategories(), it -> CategoryID.from(it.getId().getCategoryId()));
    }

    public Set<GenreID> getGenresID() {
        return CollectionUtils.mapTo(getGenres(), it -> GenreID.from(it.getId().getGenreId()));
    }

    public Set<CastMemberID> getCastMembersID() {
        return CollectionUtils.mapTo(getCastMembers(), it -> CastMemberID.from(it.getId().getCastMemberId()));
    }
    public Set<VideoCastMemberJpaEntity> getCastMembers() {
        return castMembers;
    }

    public Set<VideoGenreJpaEntity> getGenres() {
        return genres;
    }

    public Set<VideoCategoryJpaEntity> getCategories() {
        return categories;
    }

    public ImageMediaJpaEntity getBanner() {
        return banner;
    }

    public ImageMediaJpaEntity getThumbnail() {
        return thumbnail;
    }

    public ImageMediaJpaEntity getThumbnailHalf() {
        return thumbnailHalf;
    }

    public AudioVideoMediaJpaEntity getVideo() {
        return video;
    }

    public AudioVideoMediaJpaEntity getTrailer() {
        return trailer;
    }

    public String getId() {
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
