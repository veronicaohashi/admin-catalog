package com.fullcycle.admin.catalog.infraestructure.services.impl;

import com.fullcycle.admin.catalog.infraestructure.configuration.json.Json;
import com.fullcycle.admin.catalog.infraestructure.services.EventService;
import org.springframework.amqp.rabbit.core.RabbitOperations;

import java.util.Objects;

public class RabbitEventService implements EventService {

    private final String exchange;
    private final String routingKey;
    private final RabbitOperations ops;

    public RabbitEventService(final String exchange, final String routingKey, final RabbitOperations ops) {
        this.exchange = Objects.requireNonNull(exchange);
        this.routingKey = Objects.requireNonNull(routingKey);
        this.ops = Objects.requireNonNull(ops);
    }

    @Override
    public void send(Object event) {
        ops.convertAndSend(exchange, routingKey, Json.writeValueAsString(event));
    }
}
