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
        final var type = VideoMediaType.of(command.mediaType())
                .orElseThrow(() -> NotFoundException.with(
                        new Error("Media type %s doesn't exist".formatted(command.mediaType()))
                ));

        final var resource = mediaResourceGateway.getResource(videoID, type)
                .orElseThrow(() -> NotFoundException.with(
                        new Error("Resource %s not found for video %s".formatted(type, videoID.getValue()))
                ));

        return MediaOutput.from(resource);
    }
}
