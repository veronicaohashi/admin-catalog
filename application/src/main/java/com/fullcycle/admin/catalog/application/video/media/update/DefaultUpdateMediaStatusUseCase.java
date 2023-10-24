package com.fullcycle.admin.catalog.application.video.media.update;

import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.video.*;

import java.util.Objects;

import static com.fullcycle.admin.catalog.domain.video.VideoMediaType.TRAILER;
import static com.fullcycle.admin.catalog.domain.video.VideoMediaType.VIDEO;

public class DefaultUpdateMediaStatusUseCase extends UpdateMediaStatusUseCase{

    private final VideoGateway videoGateway;

    public DefaultUpdateMediaStatusUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public void execute(final UpdateMediaStatusCommand command) {
        final var videoId = VideoID.from(command.videoId());
        final var resourceId = command.resourceId();
        final var folder = command.folder();
        final var filename = command.filename();

        final var video = videoGateway.findById(videoId)
                .orElseThrow(() -> NotFoundException.with(Video.class, videoId));

        final var encodedPath = "%s/%s".formatted(folder, filename);

        if (matches(resourceId, video.getVideo().orElse(null))) {
            updateVideo(VIDEO, command.status(), video, encodedPath);
        } else if (matches(resourceId, video.getTrailer().orElse(null))) {
            updateVideo(TRAILER, command.status(), video, encodedPath);
        }
    }

    private boolean matches(final String resourceId, final AudioVideoMedia media) {
        if (media == null) {
            return false;
        }

        return media.id().equals(resourceId);
    }

    private void updateVideo(
            final VideoMediaType type,
            final MediaStatus status,
            final Video video,
            final String encodedPath
    ) {
        switch (status) {
            case PENDING -> {}
            case PROCESSING -> video.processing(type);
            case COMPLETED -> video.completed(type, encodedPath);
        }

        this.videoGateway.update(video);
    }
}
