package com.fullcycle.admin.catalog.domain.video;

public interface MediaResourceGateway {
    AudioVideoMedia storeAudioVideo(VideoID id, Resource resource);

    ImageMedia storeImage(VideoID id, Resource resource);

    void clearResource(VideoID id);
}
