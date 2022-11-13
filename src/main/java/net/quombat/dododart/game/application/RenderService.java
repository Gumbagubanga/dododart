package net.quombat.dododart.game.application;

import net.quombat.dododart.game.application.ports.in.RenderUseCase;
import net.quombat.dododart.game.application.ports.out.GamePersistencePort;
import net.quombat.dododart.game.domain.Game;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class RenderService implements RenderUseCase {

    private final GamePersistencePort persistencePort;

    @Override
    public Game fetchGame() {
        return persistencePort.fetch();
    }
}
