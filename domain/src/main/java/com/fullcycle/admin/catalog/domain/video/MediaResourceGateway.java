package com.fullcycle.admin.catalog.domain.video;

import com.fullcycle.admin.catalog.domain.resource.Resource;

import java.util.Optional;

public interface MediaResourceGateway {
    AudioVideoMedia storeAudioVideo(VideoID id, VideoResource resource);

    ImageMedia storeImage(VideoID id, VideoResource resource);

    void clearResource(VideoID id);

    Optional<Resource> getResource(VideoID videoID, VideoMediaType type);
}
