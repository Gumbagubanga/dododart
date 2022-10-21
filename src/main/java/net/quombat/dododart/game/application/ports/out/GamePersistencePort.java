package net.quombat.dododart.game.application.ports.out;

import net.quombat.dododart.game.domain.Game;

public interface GamePersistencePort {
    void save(Game game);

    Game fetch();
}
