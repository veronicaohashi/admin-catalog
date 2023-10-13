package com.fullcycle.admin.catalog.infraestructure.configuration;

import com.fullcycle.admin.catalog.infraestructure.configuration.properties.GoogleCloudProperties;
import com.fullcycle.admin.catalog.infraestructure.configuration.properties.GoogleStorageProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"development", "production"})
public class GoogleCloudConfig {

    @Bean
    @ConfigurationProperties("google.cloud")
    public GoogleCloudProperties googleCloudProperties() {
        return new GoogleCloudProperties();
    }

    @Bean
    @ConfigurationProperties("google.cloud.storage.adm-videos")
    public GoogleStorageProperties googleStorageProperties() { return new GoogleStorageProperties(); }
}
