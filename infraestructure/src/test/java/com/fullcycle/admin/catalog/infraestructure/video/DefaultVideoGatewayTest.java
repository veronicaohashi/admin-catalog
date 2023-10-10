package com.fullcycle.admin.catalog.infraestructure.video;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.Fixture;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.video.AudioVideoMedia;
import com.fullcycle.admin.catalog.domain.video.ImageMedia;
import com.fullcycle.admin.catalog.domain.video.Video;
import com.fullcycle.admin.catalog.infraestructure.video.persistence.VideoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.Set;

@IntegrationTest
class DefaultVideoGatewayTest {

    @Autowired
    private DefaultVideoGateway videoGateway;

    @Autowired
    private CategoryGateway categoryGateway;

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private CastMemberGateway castMemberGateway;

    @Autowired
    private VideoRepository videoRepository;

    @Test
    @Transactional
    void givenAValidVideo_whenCallsCreate_thenPersistIt() {
        final var categories = categoryGateway.create(Fixture.Categories.scienceFiction());
        final var genres = genreGateway.create(Fixture.Genres.dystopian());
        final var members = castMemberGateway.create(Fixture.CastMembers.jenniferLawrence());
        final var expectedTitle = Fixture.Videos.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchYear = Fixture.year();
        final var expectedDuration = Fixture.Videos.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.of(categories.getId());
        final var expectedGenres = Set.of(genres.getId());
        final var expectedMembers = Set.of(members.getId());
        final AudioVideoMedia expectedVideo = AudioVideoMedia.with("123", "video", "/media/video");
        final AudioVideoMedia expectedTrailer = AudioVideoMedia.with("123", "trailer", "/media/trailer");
        final ImageMedia expectedBanner = ImageMedia.with("123", "banner", "/media/banner");
        final ImageMedia expectedThumb = ImageMedia.with("123", "thumb", "/media/thumb");
        final ImageMedia expectedThumbHalf = ImageMedia.with("123", "thumbHalf", "/media/thumbHalf");

        final var video = Video.newVideo(
                        expectedTitle,
                        expectedDescription,
                        expectedLaunchYear,
                        expectedDuration,
                        expectedRating,
                        expectedOpened,
                        expectedPublished,
                        expectedCategories,
                        expectedGenres,
                        expectedMembers
                )
                .setVideo(expectedVideo)
                .setTrailer(expectedTrailer)
                .setBanner(expectedBanner)
                .setThumbnail(expectedThumb)
                .setThumbnailHalf(expectedThumbHalf);

        final var result = videoGateway.create(video);

        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(expectedTitle, result.getTitle());
        Assertions.assertEquals(expectedDescription, result.getDescription());
        Assertions.assertEquals(expectedLaunchYear, result.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, result.getDuration());
        Assertions.assertEquals(expectedOpened, result.isOpened());
        Assertions.assertEquals(expectedPublished, result.isPublished());
        Assertions.assertEquals(expectedRating, result.getRating());
        Assertions.assertEquals(expectedCategories, result.getCategories());
        Assertions.assertEquals(expectedGenres, result.getGenres());
        Assertions.assertEquals(expectedMembers, result.getCastMembers());
        Assertions.assertEquals(expectedVideo.name(), result.getVideo().get().name());
        Assertions.assertEquals(expectedTrailer.name(), result.getTrailer().get().name());
        Assertions.assertEquals(expectedBanner.name(), result.getBanner().get().name());
        Assertions.assertEquals(expectedThumb.name(), result.getThumbnail().get().name());
        Assertions.assertEquals(expectedThumbHalf.name(), result.getThumbnailHalf().get().name());

        final var persistedVideo = videoRepository.findById(result.getId().getValue()).get();

        Assertions.assertEquals(expectedTitle, persistedVideo.getTitle());
        Assertions.assertEquals(expectedDescription, persistedVideo.getDescription());
        Assertions.assertEquals(expectedLaunchYear, Year.of(persistedVideo.getYearLaunched()));
        Assertions.assertEquals(expectedDuration, persistedVideo.getDuration());
        Assertions.assertEquals(expectedOpened, persistedVideo.isOpened());
        Assertions.assertEquals(expectedPublished, persistedVideo.isPublished());
        Assertions.assertEquals(expectedRating, persistedVideo.getRating());
        Assertions.assertEquals(expectedCategories, persistedVideo.getCategoriesID());
        Assertions.assertEquals(expectedGenres, persistedVideo.getGenresID());
        Assertions.assertEquals(expectedMembers, persistedVideo.getCastMembersID());
        Assertions.assertEquals(expectedVideo.name(), persistedVideo.getVideo().getName());
        Assertions.assertEquals(expectedTrailer.name(), persistedVideo.getTrailer().getName());
        Assertions.assertEquals(expectedBanner.name(), persistedVideo.getBanner().getName());
        Assertions.assertEquals(expectedThumb.name(), persistedVideo.getThumbnail().getName());
        Assertions.assertEquals(expectedThumbHalf.name(), persistedVideo.getThumbnailHalf().getName());
    }

    @Test
    @Transactional
    void givenAValidVideoWithoutRelations_whenCallsCreate_thenPersistIt() {
        final var expectedTitle = Fixture.Videos.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchYear = Fixture.year();
        final var expectedDuration = Fixture.Videos.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedMembers = Set.<CastMemberID>of();

        final var video = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchYear,
                expectedDuration,
                expectedRating,
                expectedOpened,
                expectedPublished,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        final var result = videoGateway.create(video);

        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(expectedTitle, result.getTitle());
        Assertions.assertEquals(expectedDescription, result.getDescription());
        Assertions.assertEquals(expectedLaunchYear, result.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, result.getDuration());
        Assertions.assertEquals(expectedOpened, result.isOpened());
        Assertions.assertEquals(expectedPublished, result.isPublished());
        Assertions.assertEquals(expectedRating, result.getRating());
        Assertions.assertEquals(expectedCategories, result.getCategories());
        Assertions.assertEquals(expectedGenres, result.getGenres());
        Assertions.assertEquals(expectedMembers, result.getCastMembers());
        Assertions.assertTrue(result.getVideo().isEmpty());
        Assertions.assertTrue(result.getTrailer().isEmpty());
        Assertions.assertTrue(result.getBanner().isEmpty());
        Assertions.assertTrue(result.getThumbnail().isEmpty());
        Assertions.assertTrue(result.getThumbnailHalf().isEmpty());

        final var persistedVideo = videoRepository.findById(result.getId().getValue()).get();

        Assertions.assertEquals(expectedTitle, persistedVideo.getTitle());
        Assertions.assertEquals(expectedDescription, persistedVideo.getDescription());
        Assertions.assertEquals(expectedLaunchYear, Year.of(persistedVideo.getYearLaunched()));
        Assertions.assertEquals(expectedDuration, persistedVideo.getDuration());
        Assertions.assertEquals(expectedOpened, persistedVideo.isOpened());
        Assertions.assertEquals(expectedPublished, persistedVideo.isPublished());
        Assertions.assertEquals(expectedRating, persistedVideo.getRating());
        Assertions.assertEquals(expectedCategories, persistedVideo.getCategoriesID());
        Assertions.assertEquals(expectedGenres, persistedVideo.getGenresID());
        Assertions.assertEquals(expectedMembers, persistedVideo.getCastMembersID());
        Assertions.assertNull(persistedVideo.getVideo());
        Assertions.assertNull(persistedVideo.getTrailer());
        Assertions.assertNull(persistedVideo.getBanner());
        Assertions.assertNull(persistedVideo.getThumbnail());
        Assertions.assertNull(persistedVideo.getThumbnailHalf());
    }

    @Test
    @Transactional
    void givenAValidVideo_whenCallsUpdate_thenPersistIt() {
        final var video = videoGateway.create(Video.newVideo(
                Fixture.Videos.title(),
                Fixture.Videos.description(),
                Fixture.year(),
                Fixture.Videos.duration(),
                Fixture.Videos.rating(),
                Fixture.bool(),
                Fixture.bool(),
                Set.of(),
                Set.of(),
                Set.of()
        ));
        final var categories = categoryGateway.create(Fixture.Categories.scienceFiction());
        final var genres = genreGateway.create(Fixture.Genres.dystopian());
        final var members = castMemberGateway.create(Fixture.CastMembers.jenniferLawrence());
        final var expectedTitle = Fixture.Videos.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchYear = Fixture.year();
        final var expectedDuration = Fixture.Videos.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.of(categories.getId());
        final var expectedGenres = Set.of(genres.getId());
        final var expectedMembers = Set.of(members.getId());
        final AudioVideoMedia expectedVideo = AudioVideoMedia.with("123", "video", "/media/video");
        final AudioVideoMedia expectedTrailer = AudioVideoMedia.with("123", "trailer", "/media/trailer");
        final ImageMedia expectedBanner = ImageMedia.with("123", "banner", "/media/banner");
        final ImageMedia expectedThumb = ImageMedia.with("123", "thumb", "/media/thumb");
        final ImageMedia expectedThumbHalf = ImageMedia.with("123", "thumbHalf", "/media/thumbHalf");

        final var updatedVideo = Video.with(video).update(
                        expectedTitle,
                        expectedDescription,
                        expectedLaunchYear,
                        expectedDuration,
                        expectedRating,
                        expectedOpened,
                        expectedPublished,
                        expectedCategories,
                        expectedGenres,
                        expectedMembers
                )
                .setVideo(expectedVideo)
                .setTrailer(expectedTrailer)
                .setBanner(expectedBanner)
                .setThumbnail(expectedThumb)
                .setThumbnailHalf(expectedThumbHalf);

        final var result = videoGateway.update(updatedVideo);
        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(expectedTitle, result.getTitle());
        Assertions.assertEquals(expectedDescription, result.getDescription());
        Assertions.assertEquals(expectedLaunchYear, result.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, result.getDuration());
        Assertions.assertEquals(expectedOpened, result.isOpened());
        Assertions.assertEquals(expectedPublished, result.isPublished());
        Assertions.assertEquals(expectedRating, result.getRating());
        Assertions.assertEquals(expectedCategories, result.getCategories());
        Assertions.assertEquals(expectedGenres, result.getGenres());
        Assertions.assertEquals(expectedMembers, result.getCastMembers());
        Assertions.assertEquals(expectedVideo.name(), result.getVideo().get().name());
        Assertions.assertEquals(expectedTrailer.name(), result.getTrailer().get().name());
        Assertions.assertEquals(expectedBanner.name(), result.getBanner().get().name());
        Assertions.assertEquals(expectedThumb.name(), result.getThumbnail().get().name());
        Assertions.assertEquals(expectedThumbHalf.name(), result.getThumbnailHalf().get().name());
        Assertions.assertTrue(video.getUpdatedAt().isBefore(result.getUpdatedAt()));

        final var persistedVideo = videoRepository.findById(result.getId().getValue()).get();

        Assertions.assertEquals(expectedTitle, persistedVideo.getTitle());
        Assertions.assertEquals(expectedDescription, persistedVideo.getDescription());
        Assertions.assertEquals(expectedLaunchYear, Year.of(persistedVideo.getYearLaunched()));
        Assertions.assertEquals(expectedDuration, persistedVideo.getDuration());
        Assertions.assertEquals(expectedOpened, persistedVideo.isOpened());
        Assertions.assertEquals(expectedPublished, persistedVideo.isPublished());
        Assertions.assertEquals(expectedRating, persistedVideo.getRating());
        Assertions.assertEquals(expectedCategories, persistedVideo.getCategoriesID());
        Assertions.assertEquals(expectedGenres, persistedVideo.getGenresID());
        Assertions.assertEquals(expectedMembers, persistedVideo.getCastMembersID());
        Assertions.assertEquals(expectedVideo.name(), persistedVideo.getVideo().getName());
        Assertions.assertEquals(expectedTrailer.name(), persistedVideo.getTrailer().getName());
        Assertions.assertEquals(expectedBanner.name(), persistedVideo.getBanner().getName());
        Assertions.assertEquals(expectedThumb.name(), persistedVideo.getThumbnail().getName());
        Assertions.assertEquals(expectedThumbHalf.name(), persistedVideo.getThumbnailHalf().getName());
        Assertions.assertTrue(video.getUpdatedAt().isBefore(persistedVideo.getUpdatedAt()));
    }
}