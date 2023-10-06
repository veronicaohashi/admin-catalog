package com.fullcycle.admin.catalog.infraestructure.video.persistence;

import com.fullcycle.admin.catalog.domain.genre.GenreID;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Table(name = "videos_genres")
@Entity(name = "VideoGenre")
public class VideoGenreJpaEntity {

    @EmbeddedId
    private VideoGenreID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("videoId")
    private VideoJpaEntity video;

    public VideoGenreJpaEntity() {
    }

    private VideoGenreJpaEntity(final VideoGenreID id, final VideoJpaEntity video) {
        this.id = id;
        this.video = video;
    }

    public static VideoGenreJpaEntity from(final VideoJpaEntity video, final GenreID genreID) {
        return new VideoGenreJpaEntity(VideoGenreID.from(video.getId(), UUID.fromString(genreID.getValue())), video);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoGenreJpaEntity that = (VideoGenreJpaEntity) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getVideo(), that.getVideo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getVideo());
    }

    public VideoGenreID getId() {
        return id;
    }

    public VideoJpaEntity getVideo() {
        return video;
    }
}
