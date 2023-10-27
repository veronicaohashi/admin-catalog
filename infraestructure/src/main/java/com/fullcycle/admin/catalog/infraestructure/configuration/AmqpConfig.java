package com.fullcycle.admin.catalog.infraestructure.configuration;

import com.fullcycle.admin.catalog.infraestructure.configuration.annotations.VideoCreatedQueue;
import com.fullcycle.admin.catalog.infraestructure.configuration.annotations.VideoEncodedQueue;
import com.fullcycle.admin.catalog.infraestructure.configuration.properties.QueueProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {

    @Bean
    @ConfigurationProperties("amqp.queues.video-created")
    @VideoCreatedQueue
    QueueProperties videoCreatedProperties() {
        return new QueueProperties();
    }

    @Bean
    @ConfigurationProperties("amqp.queues.video-encoded")
    @VideoEncodedQueue
    QueueProperties videoEncodedProperties() {
        return new QueueProperties();
    }
}
