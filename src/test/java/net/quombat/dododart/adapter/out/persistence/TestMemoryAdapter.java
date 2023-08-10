package net.quombat.dododart.adapter.out.persistence;

import net.quombat.dododart.application.ports.out.GamePersistencePort;

public class TestMemoryAdapter {

    public static GamePersistencePort create() {
        return new GameInMemoryAdapter();
    }
}
