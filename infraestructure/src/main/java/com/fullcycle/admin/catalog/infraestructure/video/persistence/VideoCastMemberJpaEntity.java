package com.fullcycle.admin.catalog.infraestructure.video.persistence;

import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;

import javax.persistence.*;
import java.util.UUID;

@Table(name = "videos_cast_members")
@Entity(name = "VideoCastMember")
public class VideoCastMemberJpaEntity {

    @EmbeddedId
    private VideoCastMemberID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("videoId")
    private VideoJpaEntity video;

    public VideoCastMemberJpaEntity() {
    }

    private VideoCastMemberJpaEntity(final VideoCastMemberID id, final VideoJpaEntity video) {
        this.id = id;
        this.video = video;
    }

    public static VideoCastMemberJpaEntity from(final VideoJpaEntity video, final CastMemberID castMemberID) {
        return new VideoCastMemberJpaEntity(
                VideoCastMemberID.from(video.getId(), UUID.fromString(castMemberID.getValue())),
                video
        );
    }

    public VideoCastMemberID getId() {
        return id;
    }

    public VideoJpaEntity getVideo() {
        return video;
    }
}
