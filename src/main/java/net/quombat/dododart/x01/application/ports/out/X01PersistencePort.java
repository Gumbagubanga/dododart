package net.quombat.dododart.x01.application.ports.out;

import net.quombat.dododart.x01.domain.X01Game;

public interface X01PersistencePort {
    void manage(X01Game game);

    X01Game fetch();
}
