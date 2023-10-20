package com.fullcycle.admin.catalog.application.video.media.get;

import com.fullcycle.admin.catalog.domain.resource.Resource;

public record MediaOutput(
        String name,
        byte[] content,
        String contentType
) {
    public static MediaOutput from(final Resource resource) {
        return new MediaOutput(resource.name(), resource.content(), resource.contentType());
    }
}
