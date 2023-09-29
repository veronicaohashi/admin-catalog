package com.fullcycle.admin.catalog.application.video.create;

import com.fullcycle.admin.catalog.domain.Identifier;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.InternalErrorException;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import com.fullcycle.admin.catalog.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalog.domain.video.Rating;
import com.fullcycle.admin.catalog.domain.video.Video;
import com.fullcycle.admin.catalog.domain.video.VideoGateway;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultCreateVideoUseCase extends CreateVideoUseCase {

    private final VideoGateway videoGateway;
    private final CategoryGateway categoryGateway;
    private final CastMemberGateway castMemberGateway;
    private final GenreGateway genreGateway;
    private final MediaResourceGateway mediaResourceGateway;

    public DefaultCreateVideoUseCase(
            final VideoGateway videoGateway,
            final CategoryGateway categoryGateway,
            final CastMemberGateway castMemberGateway,
            final GenreGateway genreGateway,
            final MediaResourceGateway mediaResourceGateway
    ) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public CreateVideoOutput execute(final CreateVideoCommand command) {
        final var launchedAt = command.launchedAt() != null ? Year.of(command.launchedAt()) : null;
        final var rating = Rating.of(command.rating()).orElse(null);
        final var categories = toIdentifier(command.categories(), CategoryID::from);
        final var genres = toIdentifier(command.genres(), GenreID::from);
        final var members = toIdentifier(command.members(), CastMemberID::from);

        final var notification = Notification.create();
        notification.append(validateCategories(categories));
        notification.append(validateGenres(genres));
        notification.append(validateCastMembers(members));

        final var video = Video.newVideo(
                command.title(),
                command.description(),
                launchedAt,
                command.duration(),
                rating,
                command.opened(),
                command.published(),
                categories,
                genres,
                members
        );

        video.validate(notification);
        if (notification.hasError()) {
            throw new NotificationException("Could not create Aggregate Video", notification);
        }

        return CreateVideoOutput.from(create(command, video));
    }

    private ValidationHandler validateCategories(final Set<CategoryID> ids) {
        return validateAggregate("categories", ids, categoryGateway::existsByIds);
    }

    private ValidationHandler validateGenres(final Set<GenreID> ids) {
        return validateAggregate("genres", ids, genreGateway::existsByIds);
    }

    private ValidationHandler validateCastMembers(final Set<CastMemberID> ids) {
        return validateAggregate("cast members", ids, castMemberGateway::existsByIds);
    }

    private Video create(final CreateVideoCommand command, final Video video) {
        final var id = video.getId();

        try {
            final var videoMedia = command.getVideo()
                    .map(it -> mediaResourceGateway.storeAudioVideo(id, it))
                    .orElse(null);

            final var trailerMedia = command.getTrailer()
                    .map(it -> mediaResourceGateway.storeAudioVideo(id, it))
                    .orElse(null);

            final var bannerMedia = command.getBanner()
                    .map(it -> mediaResourceGateway.storeImage(id, it))
                    .orElse(null);

            final var thumbnailMedia = command.getThumbnail()
                    .map(it -> mediaResourceGateway.storeImage(id, it))
                    .orElse(null);

            final var thumbnailHalfMedia = command.getThumbnailHalf()
                    .map(it -> mediaResourceGateway.storeImage(id, it))
                    .orElse(null);

            return videoGateway.create(
                    video
                            .setVideo(videoMedia)
                            .setTrailer(trailerMedia)
                            .setBanner(bannerMedia)
                            .setThumbnail(thumbnailMedia)
                            .setThumbnailHalf(thumbnailHalfMedia)
            );
        } catch (final Throwable t) {
            mediaResourceGateway.clearResource(id);
            throw InternalErrorException.with(
                    "An error on create video was observed [videoId:%s]".formatted(id.getValue()), t
            );
        }
    }

    private <T extends Identifier> ValidationHandler validateAggregate(
            final String aggregate,
            Set<T> ids,
            Function<Iterable<T>, List<T>> existsByIds
    ) {
        final var notification = Notification.create();
        if (ids == null || ids.isEmpty()) {
            return notification;
        }

        final var retrievedIds = existsByIds.apply(ids);
        if (ids.size() != retrievedIds.size()) {
            final var missingIds = new ArrayList<>(ids);
            missingIds.removeAll(retrievedIds);

            final var missingIdsMessage = missingIds.stream()
                    .map(Identifier::getValue)
                    .collect(Collectors.joining(", "));

            notification.append(new Error("Some %s could not be found: %s".formatted(aggregate, missingIdsMessage)));
        }

        return notification;
    }

    private <T> Set<T> toIdentifier(final Set<String> ids, final Function<String, T> mapper) {
        return ids.stream()
                .map(mapper)
                .collect(Collectors.toSet());
    }
}
