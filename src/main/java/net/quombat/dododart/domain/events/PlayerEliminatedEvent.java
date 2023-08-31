package net.quombat.dododart.domain.events;

import net.quombat.dododart.domain.DomainEvent;
import net.quombat.dododart.domain.Player;

public record PlayerEliminatedEvent(Player eliminated,
                                    Player eliminator) implements DomainEvent {
}
