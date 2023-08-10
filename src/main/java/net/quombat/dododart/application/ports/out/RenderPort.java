package net.quombat.dododart.application.ports.out;

import net.quombat.dododart.domain.DomainEvent;

import java.util.List;

public interface RenderPort {
    void render(List<? super DomainEvent> domainEvents);
}
