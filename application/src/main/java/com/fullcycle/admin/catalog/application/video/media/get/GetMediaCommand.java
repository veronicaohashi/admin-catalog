package com.fullcycle.admin.catalog.application.video.media.get;

public record GetMediaCommand(
        String videoId,
        String mediaType
) {
    public static GetMediaCommand with(final String videoId, final String mediaType) {
        return new GetMediaCommand(videoId, mediaType);
    }
}
