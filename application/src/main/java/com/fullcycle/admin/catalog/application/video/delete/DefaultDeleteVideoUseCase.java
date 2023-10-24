package com.fullcycle.admin.catalog.application.video.delete;

import com.fullcycle.admin.catalog.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalog.domain.video.VideoGateway;
import com.fullcycle.admin.catalog.domain.video.VideoID;

import java.util.Objects;

public class DefaultDeleteVideoUseCase extends DeleteVideoUseCase {
    private final VideoGateway videoGateway;
    private final MediaResourceGateway mediaResourceGateway;

    public DefaultDeleteVideoUseCase(final VideoGateway videoGateway, final MediaResourceGateway mediaResourceGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public void execute(String id) {
        final var videoID = VideoID.from(id);
        videoGateway.deleteById(videoID);
        mediaResourceGateway.clearResource(videoID);
    }
}
