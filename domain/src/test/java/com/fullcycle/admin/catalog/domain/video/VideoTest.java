package com.fullcycle.admin.catalog.domain.video;

import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.Collections;
import java.util.Set;

class VideoTest {

    @Test
    void givenValidParams_whenCallNewVideo_thenInstantiateAVideo() {
        final var expectedTitle = "System Design Interviews";
        final var expectedDescription = """
                Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                Para acessar todas as aulas, lives e desafios, acesse:
                https://imersao.fullcycle.com.br/
                """;
        final var expectedLaunchedAt = Year.of(2023);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var video = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedRating,
                expectedOpened,
                expectedPublished,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        Assertions.assertNotNull(video.getId());
        Assertions.assertEquals(expectedTitle, video.getTitle());
        Assertions.assertEquals(expectedDescription, video.getDescription());
        Assertions.assertEquals(expectedLaunchedAt, video.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, video.getDuration());
        Assertions.assertEquals(expectedOpened, video.isOpened());
        Assertions.assertEquals(expectedPublished, video.isPublished());
        Assertions.assertEquals(expectedRating, video.getRating());
        Assertions.assertEquals(expectedCategories, video.getCategories());
        Assertions.assertEquals(expectedGenres, video.getGenres());
        Assertions.assertEquals(expectedMembers, video.getCastMembers());
        Assertions.assertTrue(video.getVideo().isEmpty());
        Assertions.assertTrue(video.getTrailer().isEmpty());
        Assertions.assertTrue(video.getBanner().isEmpty());
        Assertions.assertTrue(video.getThumbnail().isEmpty());
        Assertions.assertTrue(video.getThumbnailHalf().isEmpty());
        Assertions.assertDoesNotThrow(() -> video.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenValidVideo_whenCallsUpdate_thenReturnUpdated() {
        final var expectedTitle = "System Design Interviews";
        final var expectedDescription = """
                Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                Para acessar todas as aulas, lives e desafios, acesse:
                https://imersao.fullcycle.com.br/
                """;
        final var expectedLaunchedAt = Year.of(2023);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var video = Video.newVideo(
                "Test Title",
                "Test Description",
                Year.of(1999),
                10,
                Rating.AGE_10,
                true,
                true,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet()
        );

        final var updatedVideo = Video.with(video).update(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedRating,
                expectedOpened,
                expectedPublished,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        Assertions.assertNotNull(video.getId());
        Assertions.assertEquals(expectedTitle, updatedVideo.getTitle());
        Assertions.assertEquals(expectedDescription, updatedVideo.getDescription());
        Assertions.assertEquals(expectedLaunchedAt, updatedVideo.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, updatedVideo.getDuration());
        Assertions.assertEquals(expectedOpened, updatedVideo.isOpened());
        Assertions.assertEquals(expectedPublished, updatedVideo.isPublished());
        Assertions.assertEquals(expectedRating, updatedVideo.getRating());
        Assertions.assertEquals(expectedCategories, updatedVideo.getCategories());
        Assertions.assertEquals(expectedGenres, updatedVideo.getGenres());
        Assertions.assertEquals(expectedMembers, updatedVideo.getCastMembers());
        Assertions.assertEquals(video.getCreatedAt(), updatedVideo.getCreatedAt());
        Assertions.assertTrue(updatedVideo.getUpdatedAt().isAfter(video.getUpdatedAt()));
        Assertions.assertTrue(updatedVideo.getVideo().isEmpty());
        Assertions.assertTrue(updatedVideo.getTrailer().isEmpty());
        Assertions.assertTrue(updatedVideo.getBanner().isEmpty());
        Assertions.assertTrue(updatedVideo.getThumbnail().isEmpty());
        Assertions.assertTrue(updatedVideo.getThumbnailHalf().isEmpty());

        Assertions.assertDoesNotThrow(() -> updatedVideo.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenValidVideo_whenCallsSetVideo_thenReturnUpdated() {
        final var expectedVideo = AudioVideoMedia.with(
                "abc",
                "Video.mp4",
                "/videos",
                "",
                MediaStatus.PENDING
        );
        final var video = Video.newVideo(
                "Test Title",
                "Test Description",
                Year.of(1999),
                10,
                Rating.AGE_10,
                true,
                true,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet()
        );

        final var updatedVideo = Video.with(video).setVideo(expectedVideo);

        Assertions.assertEquals(expectedVideo, updatedVideo.getVideo().get());
        Assertions.assertTrue(updatedVideo.getUpdatedAt().isAfter(video.getUpdatedAt()));
        Assertions.assertDoesNotThrow(() -> updatedVideo.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenValidVideo_whenCallsSetTrailer_thenReturnUpdated() {
        final var expectedTrailer = AudioVideoMedia.with(
                "abc",
                "Trailer.mp4",
                "/trailers",
                "",
                MediaStatus.PENDING
        );
        final var video = Video.newVideo(
                "Test Title",
                "Test Description",
                Year.of(1999),
                10,
                Rating.AGE_10,
                true,
                true,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet()
        );

        final var updatedVideo = Video.with(video).setTrailer(expectedTrailer);

        Assertions.assertEquals(expectedTrailer, updatedVideo.getTrailer().get());
        Assertions.assertTrue(updatedVideo.getUpdatedAt().isAfter(video.getUpdatedAt()));
        Assertions.assertDoesNotThrow(() -> updatedVideo.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenValidVideo_whenCallsSetBanner_thenReturnUpdated() {
        final var expectedBanner = ImageMedia.with(
                "abc",
                "Banner.jpg",
                "/banners"
        );
        final var video = Video.newVideo(
                "Test Title",
                "Test Description",
                Year.of(1999),
                10,
                Rating.AGE_10,
                true,
                true,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet()
        );

        final var updatedVideo = Video.with(video).setBanner(expectedBanner);

        Assertions.assertEquals(expectedBanner, updatedVideo.getBanner().get());
        Assertions.assertTrue(updatedVideo.getUpdatedAt().isAfter(video.getUpdatedAt()));
        Assertions.assertDoesNotThrow(() -> updatedVideo.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenValidVideo_whenCallsSetThumbnail_thenReturnUpdated() {
        final var expectedThumbnail = ImageMedia.with(
                "abc",
                "Thumbnail.jpg",
                "/thumbnails"
        );
        final var video = Video.newVideo(
                "Test Title",
                "Test Description",
                Year.of(1999),
                10,
                Rating.AGE_10,
                true,
                true,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet()
        );

        final var updatedVideo = Video.with(video).setThumbnail(expectedThumbnail);

        Assertions.assertEquals(expectedThumbnail, updatedVideo.getThumbnail().get());
        Assertions.assertTrue(updatedVideo.getUpdatedAt().isAfter(video.getUpdatedAt()));
        Assertions.assertDoesNotThrow(() -> updatedVideo.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenValidVideo_whenCallsSetThumbnailHalf_tehnReturnUpdated() {
        final var expectedThumbnailHalf = ImageMedia.with(
                "abc",
                "ThumbnailHalf.jpg",
                "/thumbnailHalf"
        );
        final var video = Video.newVideo(
                "Test Title",
                "Test Description",
                Year.of(1999),
                10,
                Rating.AGE_10,
                true,
                true,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet()
        );

        final var updatedVideo = Video.with(video).setThumbnailHalf(expectedThumbnailHalf);

        Assertions.assertEquals(expectedThumbnailHalf, updatedVideo.getThumbnailHalf().get());
        Assertions.assertTrue(updatedVideo.getUpdatedAt().isAfter(video.getUpdatedAt()));
        Assertions.assertDoesNotThrow(() -> updatedVideo.validate(new ThrowsValidationHandler()));
    }
}
