package net.quombat.dododart.domain;

public record PlayerEliminatedEvent(Player eliminated,
                                    Player eliminator) implements DomainEvent {
}
