package com.fullcycle.admin.catalog.infraestructure.services;

import com.fullcycle.admin.catalog.domain.video.Resource;

import java.util.List;

public interface StorageService {
    void store(String id, Resource resource);

    Resource get(String id);

    List<String> list(String prefix);

    void deleteAll(List<String> ids);
}
