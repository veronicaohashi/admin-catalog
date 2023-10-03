package com.fullcycle.admin.catalog.application.video.update;

import com.fullcycle.admin.catalog.domain.video.Video;

public record UpdateVideoOutput(
        String id
) {
    public static UpdateVideoOutput from(final Video video) {
        return new UpdateVideoOutput(video.getId().getValue());
    }


}
