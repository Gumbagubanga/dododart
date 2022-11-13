package net.quombat.dododart.game.adapter.out.persistence;

import net.quombat.dododart.game.application.ports.out.GamePersistencePort;

public class TestMemoryAdapter {

    public static GamePersistencePort create() {
        return new GameInMemoryAdapter();
    }
}
