package com.fullcycle.admin.catalog.domain.video;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AudioVideoMediaTest {

    @Test
    void givenValidParams_whenCallsNewAudioVideo_thenReturnInstance() {
        final var expectedChecksum = "abc";
        final var expectedName = "Video.mp4";
        final var expectedRawLocation = "/videos";
        final var expectedEncodedLocation = "/videos/encoded";
        final var expectedMediaStatus = MediaStatus.COMPLETED;

        final var video = AudioVideoMedia.with(
                expectedChecksum,
                expectedName,
                expectedRawLocation,
                expectedEncodedLocation,
                expectedMediaStatus
        );

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
                expectedLocation,
                "/videos/encoded",
                MediaStatus.COMPLETED
        );

        final var video2 = AudioVideoMedia.with(
                expectedChecksum,
                "Video2.mp4",
                expectedLocation,
                "/videos/encoded",
                MediaStatus.COMPLETED
        );


        Assertions.assertEquals(video1, video2);
        Assertions.assertNotSame(video1, video2);
    }

    @Test
    void givenInvalidParams_whenCallsWith_thenReturnError() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with(null, "Random", "/videos", "/videos", MediaStatus.PENDING)
        );

        Assertions.assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with("abc", null, "/videos", "/videos", MediaStatus.PENDING)
        );

        Assertions.assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with("abc", "Random", null, "/videos", MediaStatus.PENDING)
        );

        Assertions.assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with("abc", "Random", "/videos", null, MediaStatus.PENDING)
        );

        Assertions.assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with("abc", "Random", "/videos", "/videos", null)
        );
    }
}