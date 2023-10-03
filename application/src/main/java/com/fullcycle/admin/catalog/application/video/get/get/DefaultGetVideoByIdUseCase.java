package com.fullcycle.admin.catalog.application.video.get.get;

import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.video.Video;
import com.fullcycle.admin.catalog.domain.video.VideoGateway;
import com.fullcycle.admin.catalog.domain.video.VideoID;

import java.util.Objects;

public class DefaultGetVideoByIdUseCase extends GetVideoByIdUseCase {

    private final VideoGateway videoGateway;

    public DefaultGetVideoByIdUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public VideoOutput execute(final String id) {
        final var videoID = VideoID.from(id);
        return videoGateway.findById(videoID)
                .map(VideoOutput::from)
                .orElseThrow(() -> NotFoundException.with(Video.class, videoID));
    }
}
