package com.fullcycle.admin.catalog.infraestructure.video;

import com.fullcycle.admin.catalog.domain.Identifier;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.utils.CollectionUtils;
import com.fullcycle.admin.catalog.domain.video.*;
import com.fullcycle.admin.catalog.infraestructure.configuration.annotations.VideoCreatedQueue;
import com.fullcycle.admin.catalog.infraestructure.services.EventService;
import com.fullcycle.admin.catalog.infraestructure.utils.SqlUtils;
import com.fullcycle.admin.catalog.infraestructure.video.persistence.VideoJpaEntity;
import com.fullcycle.admin.catalog.infraestructure.video.persistence.VideoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fullcycle.admin.catalog.domain.utils.CollectionUtils.mapTo;
import static com.fullcycle.admin.catalog.domain.utils.CollectionUtils.nullIfEmpty;

@Component
public class DefaultVideoGateway implements VideoGateway {

    private final EventService eventService;
    private final VideoRepository videoRepository;

    public DefaultVideoGateway(
            @VideoCreatedQueue final EventService eventService,
            final VideoRepository videoRepository) {
        this.eventService = Objects.requireNonNull(eventService);
        this.videoRepository = Objects.requireNonNull(videoRepository);
    }

    @Override
    @Transactional
    public Video create(final Video video) {
        return save(video);
    }

    @Override
    @Transactional
    public Video update(final Video video) {
        return save(video);
    }

    @Override
    public void deleteById(final VideoID id) {
        final var videoID = id.getValue();
        if (videoRepository.existsById(videoID)) {
            videoRepository.deleteById(videoID);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Video> findById(final VideoID id) {
        return videoRepository.findById(id.getValue())
                .map(VideoJpaEntity::toAggregate);
    }

    @Override
    public Pagination<VideoPreview> findAll(final VideoSearchQuery query) {
        final var page = PageRequest.of(
                query.page(),
                query.perPage(),
                Sort.by(Sort.Direction.fromString(query.direction()), query.sort())
        );

        final var result = videoRepository.findAll(
                SqlUtils.like(query.terms()),
                nullIfEmpty(mapTo(query.categories(), Identifier::getValue)),
                nullIfEmpty(mapTo(query.genres(), Identifier::getValue)),
                nullIfEmpty(mapTo(query.castMembers(), Identifier::getValue)),
                page
        );

        return new Pagination<>(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.toList()
        );
    }

    private Video save(final Video video) {
        final var result =  videoRepository.save(VideoJpaEntity.from(video))
                .toAggregate();


        video.publishDomainEvents(eventService::send);

        return result;
    }
}
