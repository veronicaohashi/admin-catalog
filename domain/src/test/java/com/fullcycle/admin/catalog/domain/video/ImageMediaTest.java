package com.fullcycle.admin.catalog.domain.video;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ImageMediaTest {

    @Test
    void givenValidParams_whenCallsNewImage_thenReturnInstance() {
        final var expectedChecksum = "abc";
        final var expectedName = "Image.png";
        final var expectedLocation = "/images";

        final var image = ImageMedia.with(
                expectedChecksum,
                expectedName,
                expectedLocation
        );

        Assertions.assertEquals(expectedChecksum, image.checksum());
        Assertions.assertEquals(expectedName, image.name());
        Assertions.assertEquals(expectedLocation, image.location());
    }

    @Test
    void givenTwoImagesWithSameChecksumAndLocation_whenCallsEquals_thenReturnTrue() {
        final var expectedChecksum = "abc";
        final var expectedLocation = "/images";

        final var image1 = ImageMedia.with(
                expectedChecksum,
                "Image1.jpg",
                expectedLocation
        );

        final var image2 = ImageMedia.with(
                expectedChecksum,
                "Image2.jpg",
                expectedLocation
        );


        Assertions.assertEquals(image1, image2);
        Assertions.assertNotSame(image1, image2);
    }

    @Test
    void givenInvalidParams_whenCallsWith_thenReturnError() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> ImageMedia.with(null, "Random", "/images")
        );

        Assertions.assertThrows(
                NullPointerException.class,
                () -> ImageMedia.with("abc", null, "/images")
        );

        Assertions.assertThrows(
                NullPointerException.class,
                () -> ImageMedia.with("abc", "Random", null)
        );
    }

}