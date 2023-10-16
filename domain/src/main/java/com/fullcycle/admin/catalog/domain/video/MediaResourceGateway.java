package com.fullcycle.admin.catalog.domain.video;

public interface MediaResourceGateway {
    AudioVideoMedia storeAudioVideo(VideoID id, VideoResource resource);

    ImageMedia storeImage(VideoID id, VideoResource resource);

    void clearResource(VideoID id);
}
