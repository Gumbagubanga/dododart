package net.quombat.dododart.x01.adapter.out.persistence;

import net.quombat.dododart.x01.application.ports.out.X01PersistencePort;
import net.quombat.dododart.x01.domain.X01Game;
import org.springframework.stereotype.Component;

@Component
class X01InMemoryAdapter implements X01PersistencePort {

    private X01Game game;

    @Override
    public void manage(X01Game game) {
        this.game = game;
    }

    @Override
    public X01Game fetch() {
        return game;
    }
}
