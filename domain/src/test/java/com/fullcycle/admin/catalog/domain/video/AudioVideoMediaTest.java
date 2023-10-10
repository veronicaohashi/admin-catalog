package com.fullcycle.admin.catalog.domain.video;

import com.fullcycle.admin.catalog.domain.utils.IdUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AudioVideoMediaTest {

    @Test
    void givenValidParams_whenCallsNewAudioVideo_thenReturnInstance() {
        final var expectedId = IdUtils.uuid();
        final var expectedChecksum = "abc";
        final var expectedName = "Video.mp4";
        final var expectedRawLocation = "/videos";
        final var expectedEncodedLocation = "/videos/encoded";
        final var expectedMediaStatus = MediaStatus.COMPLETED;

        final var video = AudioVideoMedia.with(
                expectedId,
                expectedChecksum,
                expectedName,
                expectedRawLocation,
                expectedEncodedLocation,
                expectedMediaStatus
        );

        Assertions.assertEquals(expectedId, video.id());
        Assertions.assertEquals(expectedChecksum, video.checksum());
        Assertions.assertEquals(expectedName, video.name());
        Assertions.assertEquals(expectedRawLocation, video.rawLocation());
        Assertions.assertEquals(expectedEncodedLocation, video.encodedPath());
        Assertions.assertEquals(expectedMediaStatus, video.status());
    }

    @Test
    void givenTwoVideosWithSameChecksumAndLocation_whenCallsEquals_thenReturnTrue() {
        final var expectedChecksum = "abc";
        final var expectedLocation = "/videos";

        final var video1 = AudioVideoMedia.with(
                expectedChecksum,
                "Video1.mp4",
                expectedLocation
        );

        final var video2 = AudioVideoMedia.with(
                expectedChecksum,
                "Video2.mp4",
                expectedLocation
        );


        Assertions.assertEquals(video1, video2);
        Assertions.assertNotSame(video1, video2);
    }

    @Test
    void givenInvalidParams_whenCallsWith_thenReturnError() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with(null, "abc", "Random", "/videos", "/videos", MediaStatus.PENDING)
        );

        Assertions.assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with("123",null, "Random", "/videos", "/videos", MediaStatus.PENDING)
        );

        Assertions.assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with("123","abc", null, "/videos", "/videos", MediaStatus.PENDING)
        );

        Assertions.assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with("123","abc", "Random", null, "/videos", MediaStatus.PENDING)
        );

        Assertions.assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with("123","abc", "Random", "/videos", null, MediaStatus.PENDING)
        );

        Assertions.assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with("123","abc", "Random", "/videos", "/videos", null)
        );
    }
}