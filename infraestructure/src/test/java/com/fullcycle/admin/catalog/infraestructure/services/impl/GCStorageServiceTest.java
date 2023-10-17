package com.fullcycle.admin.catalog.infraestructure.services.impl;

import com.fullcycle.admin.catalog.domain.Fixture;
import com.fullcycle.admin.catalog.domain.resource.Resource;
import com.fullcycle.admin.catalog.domain.utils.IdUtils;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static com.google.cloud.storage.Storage.BlobListOption.prefix;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GCStorageServiceTest {

    private Storage storage;

    private String bucket = "fc3_test";
    private GCStorageService service;

    @BeforeEach
    public void setUp() {
        storage = Mockito.mock(Storage.class);
        service = new GCStorageService(bucket, storage);
    }

    @Test
    void givenValidResource_whenCallsStore_thenStoreIt() {
        final var expectedName = IdUtils.uuid();
        final var expectedResource = Fixture.Videos.videoResource();
        final var blob = mockBlob(expectedName, expectedResource);
        doReturn(blob).when(storage).create(any(BlobInfo.class), any());

        service.store(expectedName, expectedResource);

        final var captor = ArgumentCaptor.forClass(BlobInfo.class);
        verify(storage, times(1)).create(captor.capture(), eq(expectedResource.content()));

        final var result = captor.getValue();
        Assertions.assertEquals(bucket, result.getBucket());
        Assertions.assertEquals(expectedName, result.getName());
        Assertions.assertEquals(expectedResource.checksum(), result.getCrc32cToHexString());
        Assertions.assertEquals(expectedResource.contentType(), result.getContentType());
    }

    @Test
    void givenValidResource_whenCallsGet_thenRetrieveIt() {
        final var expectedName = IdUtils.uuid();
        final var expectedResource = Fixture.Videos.videoResource();
        final var blob = mockBlob(expectedName, expectedResource);
        doReturn(blob).when(storage).get(anyString(), anyString());

        final var resource = service.get(expectedName).get();

        Assertions.assertEquals(expectedResource, resource);
        verify(storage, times(1)).get(eq(bucket), eq(expectedName));
    }

    @Test
    void givenInvalidResource_whenCallsGet_thenRetrieveEmpty() {
        final var expectedName = IdUtils.uuid();
        doReturn(null).when(storage).get(anyString(), anyString());

        final var resource = service.get(expectedName);
        Assertions.assertTrue(resource.isEmpty());
        verify(storage, times(1)).get(eq(bucket), eq(expectedName));
    }

    @Test
    void givenValidPrefix_whenCallsList_thenRetrieveIt() {
        final var expectedPrefix = "media_";

        final var expectedVideoName = expectedPrefix + IdUtils.uuid();
        final var expectedVideoResource = Fixture.Videos.videoResource();
        final var videoBlob = mockBlob(expectedVideoName, expectedVideoResource);

        final var expectedBannerName = expectedPrefix + IdUtils.uuid();
        final var expectedBannerResource = Fixture.Videos.bannerResource();
        final var bannerBlob = mockBlob(expectedBannerName, expectedBannerResource);

        final var expectedNames = List.of(expectedVideoName, expectedBannerName);
        final var page = Mockito.mock(Page.class);
        doReturn(page).when(storage).list(anyString(), any());
        doReturn(List.of(videoBlob, bannerBlob)).when(page).iterateAll();

        final var result = service.list(expectedPrefix);

        Assertions.assertTrue(expectedNames.containsAll(result));
        verify(storage, times(1)).list(eq(bucket), eq(prefix(expectedPrefix)));
    }

    @Test
    void givenValidNames_whenCallsDelete_thenDeleteAll() {
        final var expectedPrefix = "media_";
        final var expectedVideoName = expectedPrefix + IdUtils.uuid();
        final var expectedBannerName = expectedPrefix + IdUtils.uuid();
        final var expectedResources = List.of(expectedVideoName, expectedBannerName);

        service.deleteAll(expectedResources);

        final var captor = ArgumentCaptor.forClass(List.class);
        verify(storage, times(1)).delete(captor.capture());

        final var result = ((List<BlobId>) captor.getValue()).stream().map(BlobId::getName).toList();
        Assertions.assertTrue(expectedResources.containsAll(result));
    }

    private Blob mockBlob(final String name, final Resource resource) {
        final var blob = Mockito.mock(Blob.class);
        when(blob.getBlobId()).thenReturn(BlobId.of(bucket, name));
        when(blob.getContent()).thenReturn(resource.content());
        when(blob.getContentType()).thenReturn(resource.contentType());
        when(blob.getName()).thenReturn(resource.name());
        when(blob.getCrc32cToHexString()).thenReturn(resource.checksum());
        return blob;
    }
}