package com.fullcycle.admin.catalog.infraestructure.video;

import com.fullcycle.admin.catalog.domain.resource.Resource;
import com.fullcycle.admin.catalog.domain.video.*;
import com.fullcycle.admin.catalog.infraestructure.configuration.properties.storage.StorageProperties;
import com.fullcycle.admin.catalog.infraestructure.services.StorageService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultMediaResourceGateway implements MediaResourceGateway {

    final StorageService service;
    final String filenamePattern;
    final String locationPattern;

    public DefaultMediaResourceGateway(final StorageService service, final StorageProperties properties) {
        this.service = service;
        this.filenamePattern = properties.getFilenamePattern();
        this.locationPattern = properties.getLocationPattern();
    }

    @Override
    public AudioVideoMedia storeAudioVideo(final VideoID id, final VideoResource videoResource) {
        final var filepath = filepath(id, videoResource.type());
        final var resource = videoResource.resource();
        service.store(filepath, resource);
        return AudioVideoMedia.with(
                resource.checksum(),
                resource.name(),
                filepath
        );
    }

    @Override
    public ImageMedia storeImage(final VideoID id, final VideoResource imageResource) {
        final var filepath = filepath(id, imageResource.type());
        final var resource = imageResource.resource();
        service.store(filepath, resource);
        return ImageMedia.with(
                resource.checksum(),
                resource.name(),
                filepath
        );
    }

    @Override
    public void clearResource(final VideoID id) {
        final var ids = service.list(folderName(id));
        service.deleteAll(ids);
    }

    @Override
    public Optional<Resource> getResource(final VideoID videoID, final VideoMediaType type) {
        return service.get(filepath(videoID, type));
    }

    private String filepath(final VideoID id, final VideoMediaType type) {
        return folderName(id)
                .concat("/")
                .concat(
                        filename(type)
                );
    }

    private String folderName(final VideoID id) {
        return locationPattern.replace("{videoId}", id.getValue());
    }

    private String filename(final VideoMediaType type) {
        return filenamePattern.replace("{type}", type.name());
    }
}
