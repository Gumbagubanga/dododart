package net.quombat.dododart.game.adapter.out.persistence;

import net.quombat.dododart.game.application.ports.out.GamePersistencePort;
import net.quombat.dododart.game.domain.Game;

import org.springframework.stereotype.Component;

@Component
class GameInMemoryAdapter implements GamePersistencePort {

    private Game game;

    @Override
    public void save(Game game) {
        this.game = game;
    }

    @Override
    public Game fetch() {
        return game;
    }
}
