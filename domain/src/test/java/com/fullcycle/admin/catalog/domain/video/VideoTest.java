package com.fullcycle.admin.catalog.domain.video;

import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.utils.InstantUtils;
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
        Assertions.assertTrue(video.getDomainEvents().isEmpty());
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
        final var expectedEvent = new VideoMediaCreated("ID", "file");

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
        video.registerEvent(expectedEvent);

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
        Assertions.assertEquals(expectedEvent, updatedVideo.getDomainEvents().get(0));

        Assertions.assertDoesNotThrow(() -> updatedVideo.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenValidVideo_whenCallsUpdateVideo_thenReturnUpdated() {
        final var expectedVideo = AudioVideoMedia.with(
                "abc",
                "Video.mp4",
                "/videos"
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

        final var updatedVideo = Video.with(video).updateVideo(expectedVideo);

        Assertions.assertEquals(expectedVideo, updatedVideo.getVideo().get());
        Assertions.assertTrue(updatedVideo.getUpdatedAt().isAfter(video.getUpdatedAt()));
        Assertions.assertDoesNotThrow(() -> updatedVideo.validate(new ThrowsValidationHandler()));
        Assertions.assertEquals(1, updatedVideo.getDomainEvents().size());

        final var event = (VideoMediaCreated) updatedVideo.getDomainEvents().get(0);
        Assertions.assertEquals(video.getId().getValue(), event.resourceId());
        Assertions.assertEquals(expectedVideo.rawLocation(), event.filepath());
        Assertions.assertNotNull(event.occurredOn());
    }

    @Test
    void givenValidVideo_whenCallsUpdateTrailer_thenReturnUpdated() {
        final var expectedTrailer = AudioVideoMedia.with(
                "abc",
                "Trailer.mp4",
                "/trailers"
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

        final var updatedVideo = Video.with(video).updateTrailer(expectedTrailer);

        Assertions.assertEquals(expectedTrailer, updatedVideo.getTrailer().get());
        Assertions.assertTrue(updatedVideo.getUpdatedAt().isAfter(video.getUpdatedAt()));
        Assertions.assertDoesNotThrow(() -> updatedVideo.validate(new ThrowsValidationHandler()));
        Assertions.assertEquals(1, updatedVideo.getDomainEvents().size());

        final var event = (VideoMediaCreated) updatedVideo.getDomainEvents().get(0);
        Assertions.assertEquals(video.getId().getValue(), event.resourceId());
        Assertions.assertEquals(expectedTrailer.rawLocation(), event.filepath());
        Assertions.assertNotNull(event.occurredOn());
    }

    @Test
    void givenValidVideo_whenCallsUpdateBanner_thenReturnUpdated() {
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

        final var updatedVideo = Video.with(video).updateBanner(expectedBanner);

        Assertions.assertEquals(expectedBanner, updatedVideo.getBanner().get());
        Assertions.assertTrue(updatedVideo.getUpdatedAt().isAfter(video.getUpdatedAt()));
        Assertions.assertDoesNotThrow(() -> updatedVideo.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenValidVideo_whenCallsUpdateThumbnail_thenReturnUpdated() {
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

        final var updatedVideo = Video.with(video).updateThumbnail(expectedThumbnail);

        Assertions.assertEquals(expectedThumbnail, updatedVideo.getThumbnail().get());
        Assertions.assertTrue(updatedVideo.getUpdatedAt().isAfter(video.getUpdatedAt()));
        Assertions.assertDoesNotThrow(() -> updatedVideo.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenValidVideo_whenCallsUpdateThumbnailHalf_tehnReturnUpdated() {
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

        final var updatedVideo = Video.with(video).updateThumbnailHalf(expectedThumbnailHalf);

        Assertions.assertEquals(expectedThumbnailHalf, updatedVideo.getThumbnailHalf().get());
        Assertions.assertTrue(updatedVideo.getUpdatedAt().isAfter(video.getUpdatedAt()));
        Assertions.assertDoesNotThrow(() -> updatedVideo.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenValidVideo_whenCallsWith_thenCreateWithoutEvents() {
        final var expectedTitle = "System Design Interviews";
        final var expectedDescription = """
                Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                Para acessar todas as aulas, lives e desafios, acesse:
                https://imersao.fullcycle.com.br/
                """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var video = Video.with(
                VideoID.unique(),
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedRating,
                expectedOpened,
                expectedPublished,
                InstantUtils.now(),
                InstantUtils.now(),
                null,
                null,
                null,
                null,
                null,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        Assertions.assertNotNull(video.getDomainEvents());
    }
}
