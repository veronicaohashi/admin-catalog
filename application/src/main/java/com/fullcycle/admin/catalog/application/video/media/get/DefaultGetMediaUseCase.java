package com.fullcycle.admin.catalog.application.video.media.get;

import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalog.domain.video.VideoID;
import com.fullcycle.admin.catalog.domain.video.VideoMediaType;

import java.util.Objects;

public class DefaultGetMediaUseCase extends GetMediaUseCase {

    final MediaResourceGateway mediaResourceGateway;

    public DefaultGetMediaUseCase(final MediaResourceGateway mediaResourceGateway) {
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public MediaOutput execute(final GetMediaCommand command) {
        final var videoID = VideoID.from(command.videoId());
        final var type = VideoMediaType.valueOf(command.mediaType());

        final var resource = mediaResourceGateway.getResource(videoID, type)
                .orElseThrow(() -> NotFoundException.with(
                        new Error("Media type %s for id %s doesn't exists".formatted(type, videoID.getValue()))
                ));

        return MediaOutput.from(resource);
    }
}
