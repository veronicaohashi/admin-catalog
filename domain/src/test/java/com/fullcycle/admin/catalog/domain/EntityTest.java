package com.fullcycle.admin.catalog.domain;

import com.fullcycle.admin.catalog.domain.events.DomainEvent;
import com.fullcycle.admin.catalog.domain.utils.InstantUtils;
import com.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class EntityTest {

    @Test
    void givenNullAsEvents_whenInstantiate_shouldBeOk() {

        final var entity = new DummyEntity(new DummyEntityID(), null);

        Assertions.assertNotNull(entity.getDomainEvents());
        Assertions.assertTrue(entity.getDomainEvents().isEmpty());
    }

    @Test
    void givenDomainEvents_whenPassInConstructor_shouldCreateADefensiveClone() {
        final var events = new ArrayList<DomainEvent>();
        events.add(new DummyEvent());

        final var entity = new DummyEntity(new DummyEntityID(), events);

        Assertions.assertNotNull(entity.getDomainEvents());
        Assertions.assertEquals(1, entity.getDomainEvents().size());

        Assertions.assertThrows(RuntimeException.class, () -> {
            final var actualEvents = entity.getDomainEvents();
            actualEvents.add(new DummyEvent());
        });
    }

    @Test
    void givenEmptyDomainEvents_whenCallsRegisterEvent_shouldAddEventToList() {
        final var entity = new DummyEntity(new DummyEntityID(), new ArrayList<>());

        entity.registerEvent(new DummyEvent());

        Assertions.assertNotNull(entity.getDomainEvents());
        Assertions.assertEquals(1, entity.getDomainEvents().size());
    }

    @Test
    void givenAFewDomainEvents_whenCallsPublishEvents_shouldCallPublisherAndClearTheList() {
        final var expectedEvents = 0;
        final var expectedSentEvents = 2;
        final var counter = new AtomicInteger(0);
        final var entity = new DummyEntity(new DummyEntityID(), new ArrayList<>());
        entity.registerEvent(new DummyEvent());
        entity.registerEvent(new DummyEvent());

        Assertions.assertEquals(2, entity.getDomainEvents().size());

        entity.publishDomainEvents(event -> counter.incrementAndGet());

        Assertions.assertNotNull(entity.getDomainEvents());
        Assertions.assertEquals(expectedEvents, entity.getDomainEvents().size());
        Assertions.assertEquals(expectedSentEvents, counter.get());
    }

    public static class DummyEvent implements DomainEvent {
        @Override
        public Instant occurredOn() {
            return InstantUtils.now();
        }
    }

    public static class DummyEntityID extends Identifier {
        @Override
        public String getValue() {
            return null;
        }
    }
    public static class DummyEntity extends Entity<DummyEntityID> {
        protected DummyEntity(DummyEntityID dummyEntityID, List<DomainEvent> domainEvents) {
            super(dummyEntityID, domainEvents);
        }

        @Override
        public void validate(ValidationHandler handler) {

        }
    }
}