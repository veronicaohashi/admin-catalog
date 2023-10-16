package com.fullcycle.admin.catalog.infraestructure.configuration.storage;

import com.fullcycle.admin.catalog.infraestructure.configuration.properties.GoogleStorageProperties;
import com.fullcycle.admin.catalog.infraestructure.services.StorageService;
import com.fullcycle.admin.catalog.infraestructure.services.impl.GCStorageService;
import com.fullcycle.admin.catalog.infraestructure.services.local.InMemoryStorageService;
import com.google.cloud.storage.Storage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class StorageConfig {

    @Bean("storageService")
    @Profile({"development", "production"})
    public StorageService gcStorageService(final GoogleStorageProperties prop, final Storage storage) {
        return new GCStorageService(prop.getBucket(), storage);
    }

    @Bean("storageService")
    @ConditionalOnMissingBean
    public StorageService inMemoryStorageService() {
        return new InMemoryStorageService();
    }
}
