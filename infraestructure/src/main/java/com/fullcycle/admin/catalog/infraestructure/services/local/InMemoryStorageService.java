package com.fullcycle.admin.catalog.infraestructure.services.local;

import com.fullcycle.admin.catalog.domain.resource.Resource;
import com.fullcycle.admin.catalog.infraestructure.services.StorageService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorageService implements StorageService {
    private final Map<String, Resource> storage;

    public InMemoryStorageService() {
        this.storage = new ConcurrentHashMap<>();
    }

    public void clean() {
        storage.clear();
    }

    public Map<String, Resource> storage() {
        return storage;
    }

    @Override
    public void store(final String id, final Resource resource) {
        storage.put(id, resource);
    }

    @Override
    public Resource get(final String id) {
        return storage.get(id);
    }

    @Override
    public List<String> list(final String prefix) {
        return storage.keySet().stream()
                .filter(it -> it.startsWith(prefix))
                .toList();
    }

    @Override
    public void deleteAll(final List<String> ids) {
        ids.forEach(storage::remove);
    }
}
