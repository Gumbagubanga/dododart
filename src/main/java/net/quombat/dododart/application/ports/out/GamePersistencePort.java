package net.quombat.dododart.application.ports.out;

import net.quombat.dododart.domain.Game;

public interface GamePersistencePort {
    void save(Game game);

    Game fetch();
}
