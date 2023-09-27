package com.fullcycle.admin.catalog.domain.video;

import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.Set;

class VideoTest {

    @Test
    void givenValidParams_whenCallNewVideo_thenInstantiateAVideo(){
        final var expectedTitle = "System Design Interviews";
        final var expectedDescription = """
                Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                Para acessar todas as aulas, lives e desafios, acesse:
                https://imersao.fullcycle.com.br/
                """;
        final var expectedLaunchedAt = Year.of(2023);
        final var expectedDuration= 120.10;
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
}
