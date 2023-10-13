package com.fullcycle.admin.catalog.infraestructure.configuration;

import com.fullcycle.admin.catalog.infraestructure.configuration.properties.GoogleCloudProperties;
import com.fullcycle.admin.catalog.infraestructure.configuration.properties.GoogleStorageProperties;
import com.google.api.gax.retrying.RetrySettings;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.threeten.bp.Duration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

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

    @Bean
    public Credentials credentials(final GoogleCloudProperties props) throws IOException {
        final var jsonBin = Base64.getDecoder().decode(props.getCredentials());
        return GoogleCredentials.fromStream(new ByteArrayInputStream(jsonBin));
    }

    @Bean
    public Storage storage(
            final Credentials credentials,
            final GoogleCloudProperties cloudProperties,
            final GoogleStorageProperties storageProperties
    ) {
        final var transportOptions = HttpTransportOptions.newBuilder()
                .setConnectTimeout(storageProperties.getConnectTimeout())
                .setReadTimeout(storageProperties.getReadTimeout())
                .build();

        final var retryOptions = RetrySettings.newBuilder()
                .setInitialRetryDelay(Duration.ofMillis(storageProperties.getRetryDelay()))
                .setMaxRetryDelay(Duration.ofMillis(storageProperties.getRetryMaxDelay()))
                .setMaxAttempts(storageProperties.getRetryMaxAttempts())
                .setRetryDelayMultiplier(storageProperties.getRetryMultiplier())
                .build();

        return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId(cloudProperties.getProjectId())
                .setTransportOptions(transportOptions)
                .setRetrySettings(retryOptions)
                .build()
                .getService();
    }
}
