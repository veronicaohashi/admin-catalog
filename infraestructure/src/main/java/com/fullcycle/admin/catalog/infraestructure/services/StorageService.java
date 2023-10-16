package com.fullcycle.admin.catalog.infraestructure.services;

import com.fullcycle.admin.catalog.domain.resource.Resource;

import java.util.List;
import java.util.Optional;

public interface StorageService {
    void store(String id, Resource resource);

    Optional<Resource> get(String id);

    List<String> list(String prefix);

    void deleteAll(List<String> ids);
}
