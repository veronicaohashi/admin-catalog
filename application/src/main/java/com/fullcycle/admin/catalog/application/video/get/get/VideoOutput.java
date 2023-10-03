package com.fullcycle.admin.catalog.application.video.get.get;

import com.fullcycle.admin.catalog.domain.Identifier;
import com.fullcycle.admin.catalog.domain.utils.CollectionUtils;
import com.fullcycle.admin.catalog.domain.video.AudioVideoMedia;
import com.fullcycle.admin.catalog.domain.video.ImageMedia;
import com.fullcycle.admin.catalog.domain.video.Rating;
import com.fullcycle.admin.catalog.domain.video.Video;

import java.time.Instant;
import java.util.Set;

public record VideoOutput(
        String id,
        String title,
        String description,
        int launchedAt,
        double duration,
        boolean opened,
        boolean published,
        Rating rating,
        Set<String> categories,
        Set<String> genres,
        Set<String> castMembers,
        AudioVideoMedia video,
        AudioVideoMedia trailer,
        ImageMedia banner,
        ImageMedia thumbnail,
        ImageMedia thumbnailHalf,
        Instant createdAt,
        Instant updatedAt
) {
    public static VideoOutput from(final Video video) {
        return new VideoOutput(
                video.getId().getValue(),
                video.getTitle(),
                video.getDescription(),
                video.getLaunchedAt().getValue(),
                video.getDuration(),
                video.isOpened(),
                video.isPublished(),
                video.getRating(),
                CollectionUtils.mapTo(video.getCategories(), Identifier::getValue),
                CollectionUtils.mapTo(video.getGenres(), Identifier::getValue),
                CollectionUtils.mapTo(video.getCastMembers(), Identifier::getValue),
                video.getVideo().orElse(null),
                video.getTrailer().orElse(null),
                video.getBanner().orElse(null),
                video.getThumbnail().orElse(null),
                video.getThumbnailHalf().orElse(null),
                video.getCreatedAt(),
                video.getUpdatedAt()
        );
    }
}
