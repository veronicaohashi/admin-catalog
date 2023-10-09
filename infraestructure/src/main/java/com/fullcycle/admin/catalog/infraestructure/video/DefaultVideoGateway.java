package com.fullcycle.admin.catalog.infraestructure.video;

import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.video.Video;
import com.fullcycle.admin.catalog.domain.video.VideoGateway;
import com.fullcycle.admin.catalog.domain.video.VideoID;
import com.fullcycle.admin.catalog.domain.video.VideoSearchQuery;
import com.fullcycle.admin.catalog.infraestructure.video.persistence.VideoJpaEntity;
import com.fullcycle.admin.catalog.infraestructure.video.persistence.VideoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

public class DefaultVideoGateway implements VideoGateway {

    private final VideoRepository videoRepository;

    public DefaultVideoGateway(final VideoRepository videoRepository) {
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
        if(videoRepository.existsById(videoID)) {
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
    public Pagination<Video> findAll(final VideoSearchQuery query) {
        return null;
    }

    private Video save(final Video video) {
        return videoRepository.save(VideoJpaEntity.from(video))
                .toAggregate();
    }
}
