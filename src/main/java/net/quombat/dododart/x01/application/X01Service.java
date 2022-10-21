package net.quombat.dododart.x01.application;

import lombok.RequiredArgsConstructor;
import net.quombat.dododart.x01.application.ports.in.CreateNewGameX01Command;
import net.quombat.dododart.x01.application.ports.in.X01UseCase;
import net.quombat.dododart.x01.application.ports.out.UartSendPort;
import net.quombat.dododart.x01.application.ports.out.X01PersistencePort;
import net.quombat.dododart.x01.domain.ButtonPressedEvent;
import net.quombat.dododart.x01.domain.DartHitEvent;
import net.quombat.dododart.x01.domain.X01Game;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class X01Service implements X01UseCase {

    private final X01PersistencePort x01MemoryPort;
    private final UartSendPort uartSendPort;

    @Override
    public X01Game createNewGame(CreateNewGameX01Command command) {
        int noOfPlayers = command.noOfPlayers();
        int targetScore = command.targetScore();
        boolean elimination = command.elimination();
        int maxRounds = command.maxRounds();

        X01Game game = new X01Game(noOfPlayers, targetScore, elimination, maxRounds);
        x01MemoryPort.manage(game);

        return game;
    }

    @Override
    public X01Game fetchGame() {
        return x01MemoryPort.fetch();
    }

    @Override
    public void hit(DartHitEvent event) {
        X01Game game = x01MemoryPort.fetch();
        if (Optional.ofNullable(game).map(X01Game::isGameOver).orElse(true)) {
            return;
        }

        game.hit(event.segment());
        if (game.isSwitchPlayerState()) {
            uartSendPort.startButtonBlink();
        }
    }

    @Override
    public void buttonPressed(ButtonPressedEvent event) {
        X01Game game = x01MemoryPort.fetch();
        if (Optional.ofNullable(game).map(X01Game::isGameOver).orElse(true)) {
            return;
        }

        game.nextPlayer();
        uartSendPort.stopButtonBlink();
    }
}
