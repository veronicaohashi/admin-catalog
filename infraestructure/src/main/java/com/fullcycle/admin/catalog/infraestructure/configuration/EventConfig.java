package com.fullcycle.admin.catalog.infraestructure.configuration;

import com.fullcycle.admin.catalog.infraestructure.configuration.annotations.VideoCreatedQueue;
import com.fullcycle.admin.catalog.infraestructure.configuration.properties.QueueProperties;
import com.fullcycle.admin.catalog.infraestructure.services.EventService;
import com.fullcycle.admin.catalog.infraestructure.services.impl.RabbitEventService;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfig {

    @Bean
    @VideoCreatedQueue
    EventService videoCreatedEventService(
            @VideoCreatedQueue final QueueProperties props,
            final RabbitOperations ops
    ){
        return new RabbitEventService(props.getExchange(), props.getRoutingKey(), ops);
    }
}
