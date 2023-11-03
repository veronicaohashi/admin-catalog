package com.fullcycle.admin.catalog.application.video.media.upload;

import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalog.domain.video.Video;
import com.fullcycle.admin.catalog.domain.video.VideoGateway;
import com.fullcycle.admin.catalog.domain.video.VideoID;

import java.util.Objects;

public class DefaultUploadMediaUseCase extends UploadMediaUseCase {

    private final MediaResourceGateway mediaResourceGateway;
    private final VideoGateway videoGateway;

    public DefaultUploadMediaUseCase(final MediaResourceGateway mediaResourceGateway, final VideoGateway videoGateway) {
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public UploadMediaOutput execute(final UploadMediaCommand command) {
        final var videoID = VideoID.from(command.videoId());
        final var resource = command.videoResource();

        final var video = videoGateway.findById(videoID)
                .orElseThrow(() -> NotFoundException.with(Video.class, videoID));

        switch (resource.type()) {
            case VIDEO ->
                video.updateVideo(mediaResourceGateway.storeAudioVideo(videoID, resource));
            case TRAILER ->
                video.updateTrailer(mediaResourceGateway.storeAudioVideo(videoID, resource));
            case BANNER ->
                video.updateBanner(mediaResourceGateway.storeImage(videoID, resource));
            case THUMBNAIL ->
                video.updateThumbnail(mediaResourceGateway.storeImage(videoID, resource));
            case THUMBNAIL_HALF ->
                video.updateThumbnailHalf(mediaResourceGateway.storeImage(videoID, resource));
        }

        return UploadMediaOutput.with(videoGateway.update(video), resource.type());
    }
}
