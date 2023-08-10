package net.quombat.dododart.adapter.out.persistence;

import net.quombat.dododart.application.ports.out.GamePersistencePort;
import net.quombat.dododart.domain.Game;

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
