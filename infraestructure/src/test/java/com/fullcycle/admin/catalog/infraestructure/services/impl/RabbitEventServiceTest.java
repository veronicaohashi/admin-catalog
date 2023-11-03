package com.fullcycle.admin.catalog.infraestructure.services.impl;

import com.fullcycle.admin.catalog.AmqpTest;
import com.fullcycle.admin.catalog.domain.video.VideoMediaCreated;
import com.fullcycle.admin.catalog.infraestructure.configuration.annotations.VideoCreatedQueue;
import com.fullcycle.admin.catalog.infraestructure.configuration.json.Json;
import com.fullcycle.admin.catalog.infraestructure.services.EventService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@AmqpTest
class RabbitEventServiceTest {

    private static final String LISTENER = "video.created";

    @Autowired
    @VideoCreatedQueue
    private EventService publisher;

    @Autowired
    private RabbitListenerTestHarness harness;


    @Test
    void shouldSendMessage() throws InterruptedException {
        final var notification = new VideoMediaCreated("source", "filepath");
        final var expectedMessage = Json.writeValueAsString(notification);

        publisher.send(notification);

        final var data = harness.getNextInvocationDataFor(LISTENER, 1, TimeUnit.SECONDS);

        Assertions.assertNotNull(data);
        Assertions.assertNotNull(data.getArguments());

        final var message = data.getArguments()[0];

        Assertions.assertEquals(expectedMessage, message);
    }

    @Component
    static class Listener {

        @RabbitListener(id = LISTENER, queues = "${amqp.queues.video-created.routing-key}")
        void onVideoCreated(@Payload String message) {
            System.out.println(message);
        }
    }

}