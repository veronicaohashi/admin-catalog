package com.fullcycle.admin.catalog.domain;

import com.fullcycle.admin.catalog.domain.castmember.CastMember;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.resource.Resource;
import com.fullcycle.admin.catalog.domain.utils.IdUtils;
import com.fullcycle.admin.catalog.domain.video.*;
import com.github.javafaker.Faker;

import java.time.Year;
import java.util.Set;

import static io.vavr.API.*;

public final class Fixture {

    private static final Faker FAKER = new Faker();

    public static String name() {
        return FAKER.name().fullName();
    }

    public static Year year() {
        return Year.of(FAKER.random().nextInt(2020, 2030));
    }

    public static Boolean bool() {
        return FAKER.bool().bool();
    }

    public static Video video() {
        return Video.newVideo(
                        Videos.title(),
                        Videos.description(),
                        Fixture.year(),
                        Videos.duration(),
                        Videos.rating(),
                        Fixture.bool(),
                        Fixture.bool(),
                        Set.of(Categories.scienceFiction().getId()),
                        Set.of(Genres.dystopian().getId()),
                        Set.of(
                                CastMembers.jenniferLawrence().getId(),
                                CastMembers.kayaScodelario().getId()
                        )
                )
                .updateVideo(Videos.audioVideo())
                .updateTrailer(Videos.audioTrailer())
                .updateBanner(Videos.imageBanner())
                .updateThumbnail(Videos.imageThumbnail())
                .updateThumbnailHalf(Videos.imageThumbnailHalf());
    }

    public static final class CastMembers {
        private static final CastMember KAYA_SCODELARIO =
                CastMember.newMember("Kaya Scodelario", CastMemberType.ACTOR);
        private static final CastMember JENNIFER_LAWRENCE =
                CastMember.newMember("Jennifer Lawrence", CastMemberType.ACTOR);
        private static final CastMember EMMA_WATSON =
                CastMember.newMember("Emma Watson", CastMemberType.ACTOR);

        public static CastMemberType type() {
            return FAKER.options().option(CastMemberType.values());
        }

        public static CastMember jenniferLawrence() {
            return CastMember.with(JENNIFER_LAWRENCE);
        }

        public static CastMember kayaScodelario() {
            return CastMember.with(KAYA_SCODELARIO);
        }

        public static CastMember emmaWatson() { return CastMember.with(EMMA_WATSON); }
    }

    public static final class Videos {
        private static final Video SYSTEM_DESIGN = Video.newVideo(
                "System Design no Mercado Livre na prática",
                description(),
                Year.of(2022),
                Videos.duration(),
                rating(),
                Fixture.bool(),
                Fixture.bool(),
                Set.of(Categories.scienceFiction().getId()),
                Set.of(Genres.dystopian().getId()),
                Set.of(CastMembers.jenniferLawrence().getId())
        );
        private static final String HARRY_POTTER = "Harry Potter";
        private static final String HUNGER_GAMES = "Hunger Games";
        private static final String MAZE_RUNNER = "Maze Runner";

        public static String harryPotter() { return HARRY_POTTER; }
        public static String hungerGames() { return HUNGER_GAMES; }
        public static String mazzeRunner() { return MAZE_RUNNER; }

        public static String title() {
            return FAKER.options().option(HARRY_POTTER, HUNGER_GAMES, MAZE_RUNNER);
        }

        public static String description() {
            return FAKER.options().option(
                    "In Harry Potter and the Sorcerer's Stone, a young wizard, Harry, discovers his magical " +
                            "heritage and embarks on a journey to Hogwarts School of Witchcraft and Wizardry, " +
                            "uncovering dark secrets and his destiny.",
                    "In The Hunger Games, Katniss Everdeen volunteers to take her sister's place in a televised fight " +
                            "to the death, representing her district in a dystopian society. She navigates a perilous " +
                            "arena and challenges the oppressive Capitol.",
                    "In The Maze Runner, Thomas awakens in a mysterious glade surrounded by a deadly maze. He and " +
                            "other amnesiac boys must find a way out while uncovering dark secrets and facing " +
                            "terrifying creatures."
            );
        }

        public static Double duration() {
            return FAKER.options().option(120.0, 130.0, 140.0, 150.0);
        }

        public static Rating rating() {
            return FAKER.options().option(Rating.values());
        }

        private static Resource createResource(final VideoMediaType type) {
            final String contentType = Match(type).of(
                    Case($(List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                    Case($(), "image/jpg")
            );

            final String checksum = IdUtils.uuid();
            final byte[] content = "Conteudo".getBytes();

            return Resource.with(checksum, content, contentType, type.name().toLowerCase());
        }

        public static Resource videoResource() {
            return createResource(VideoMediaType.VIDEO);
        }
        public static Resource trailerResource() {
            return createResource(VideoMediaType.TRAILER);
        }
        public static Resource bannerResource() {
            return createResource(VideoMediaType.BANNER);
        }
        public static Resource thumbnailResource() {
            return createResource(VideoMediaType.THUMBNAIL);
        }
        public static Resource thumbnailHalfResource() { return createResource(VideoMediaType.THUMBNAIL_HALF); }

        private static AudioVideoMedia createAudioVideoMedia(final VideoMediaType type) {
            final var checksum = IdUtils.uuid();
            return AudioVideoMedia.with(
                    checksum,
                    type.name().toLowerCase(),
                    "/videos/" + checksum
            );
        }
        public static AudioVideoMedia audioVideo() { return createAudioVideoMedia(VideoMediaType.VIDEO); }

        public static AudioVideoMedia audioTrailer() { return createAudioVideoMedia(VideoMediaType.TRAILER); }

        private static ImageMedia createImageMedia(final VideoMediaType type) {
            final var checksum = IdUtils.uuid();
            return ImageMedia.with(
                    checksum,
                    type.name().toLowerCase(),
                    "/images/" + checksum
            );
        }

        public static ImageMedia imageBanner() { return createImageMedia(VideoMediaType.BANNER); }
        public static ImageMedia imageThumbnail() { return createImageMedia(VideoMediaType.THUMBNAIL); }
        public static ImageMedia imageThumbnailHalf() { return createImageMedia(VideoMediaType.THUMBNAIL_HALF); }

        public static Video systemDesign() {
            return Video.with(SYSTEM_DESIGN)
                    .updateVideo(Videos.audioVideo())
                    .updateTrailer(Videos.audioTrailer())
                    .updateBanner(Videos.imageBanner())
                    .updateThumbnail(Videos.imageThumbnail())
                    .updateThumbnailHalf(Videos.imageThumbnailHalf());
        }
    }

    public static final class Categories {
        public static final Category SCIENCE_FICTION = Category.newCategory("Science Fiction", "", true);
        public static final Category ACTION = Category.newCategory("ACTION", "", true);

        public static Category scienceFiction() {
            return SCIENCE_FICTION.clone();
        }

        public static Category action() {
            return ACTION.clone();
        }
    }

    public static final class Genres {
        public static final Genre DYSTOPIAN = Genre.newGenre("Dystopian", true);

        public static Genre dystopian() {
            return Genre.with(DYSTOPIAN);
        }
    }
}
