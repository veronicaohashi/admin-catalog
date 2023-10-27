package com.fullcycle.admin.catalog.domain;

import com.fullcycle.admin.catalog.domain.events.DomainEvent;
import com.fullcycle.admin.catalog.domain.events.DomainEventPublisher;
import com.fullcycle.admin.catalog.domain.validation.ValidationHandler;

import java.util.*;

import static java.util.Collections.emptyList;

public abstract class Entity<ID extends Identifier> {

    protected final ID id;
    private final List<DomainEvent> domainEvents;

    protected Entity(final ID id, final List<DomainEvent> domainEvents) {
        Objects.requireNonNull(id, "'id' should not be null");
        this.id = id;
        this.domainEvents = new ArrayList<>(domainEvents == null ? emptyList() :  domainEvents);
    }

    public abstract void validate(ValidationHandler handler);

    public ID getId() {
        return id;
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void publishDomainEvents(final DomainEventPublisher publisher) {
        if(publisher == null ) return;

        getDomainEvents()
                .forEach(publisher::publishEvent);

        domainEvents.clear();
    }

    public void registerEvent(final DomainEvent domainEvent) {
        if(domainEvent == null) return;
        domainEvents.add(domainEvent);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
