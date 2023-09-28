package com.fullcycle.admin.catalog.domain.video;

import com.fullcycle.admin.catalog.domain.pagination.Pagination;

import java.util.Optional;

public interface VideoGateway {

    Video create(Video video);

    Video update(Video video);

    void delete(VideoID videoID);

    Optional<Video> findById(VideoID videoID);

    Pagination<Video> findAll(VideoSearchQuery query);
}
