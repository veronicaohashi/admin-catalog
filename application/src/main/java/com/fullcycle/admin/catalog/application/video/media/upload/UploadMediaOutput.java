package com.fullcycle.admin.catalog.application.video.media.upload;

import com.fullcycle.admin.catalog.domain.video.Video;
import com.fullcycle.admin.catalog.domain.video.VideoMediaType;

public record UploadMediaOutput(
        String videoId,
        VideoMediaType mediaType
) {
    public static UploadMediaOutput with(final Video video, final VideoMediaType mediaType) {
        return new UploadMediaOutput(video.getId().getValue(), mediaType);
    }
}
