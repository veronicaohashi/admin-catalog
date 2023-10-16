package com.fullcycle.admin.catalog.infraestructure.services.impl;

import com.fullcycle.admin.catalog.domain.resource.Resource;
import com.fullcycle.admin.catalog.domain.utils.IdUtils;
import com.fullcycle.admin.catalog.infraestructure.services.StorageService;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class GCStorageService implements StorageService {

    private final String bucket;
    private final Storage storage;

    public GCStorageService(final String bucket, final Storage storage) {
        this.bucket = bucket;
        this.storage = storage;
    }

    @Override
    public void store(final String id, final Resource resource) {
        final var blob = BlobInfo.newBuilder(bucket, id)
                .setContentType(resource.contentType())
                .setCrc32cFromHexString(resource.checksum())
                .build();
        storage.create(blob, resource.content());
    }

    @Override
    public Optional<Resource> get(final String id) {
        return Optional.ofNullable(storage.get(bucket, id))
                .map(blob -> Resource.with(
                        blob.getCrc32cToHexString(),
                        blob.getContent(),
                        blob.getContentType(),
                        blob.getName()
                ));
    }

    @Override
    public List<String> list(final String prefix) {
        final var blobs = storage.list(bucket, Storage.BlobListOption.prefix(prefix));
        return StreamSupport.stream(blobs.iterateAll().spliterator(), false)
                .map(BlobInfo::getBlobId)
                .map(BlobId::getName)
                .toList();
    }

    @Override
    public void deleteAll(final List<String> ids) {
        final var blobs = ids.stream()
                .map(it -> BlobId.of(bucket, it))
                .toList();

        storage.delete(blobs);
    }
}
