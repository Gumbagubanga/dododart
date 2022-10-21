package net.quombat.dododart.game.application;

import net.quombat.dododart.game.application.ports.in.CreateNewGameCommand;
import net.quombat.dododart.game.application.ports.in.GameUseCase;
import net.quombat.dododart.game.application.ports.out.GamePersistencePort;
import net.quombat.dododart.game.application.ports.out.UartSendPort;
import net.quombat.dododart.game.domain.ButtonPressedEvent;
import net.quombat.dododart.game.domain.DartHitEvent;
import net.quombat.dododart.game.domain.Game;
import net.quombat.dododart.game.domain.GameState;
import net.quombat.dododart.game.domain.Rules;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class GameService implements GameUseCase {

    private final GamePersistencePort persistencePort;
    private final UartSendPort uartSendPort;

    @Override
    public Game createNewGame(CreateNewGameCommand command) {
        int noOfPlayers = command.noOfPlayers();
        Rules rules = command.rules();
        int maxRounds = command.maxRounds();

        Game game = new Game(rules, noOfPlayers, maxRounds);
        persistencePort.save(game);

        return game;
    }

    @Override
    public Game fetchGame() {
        return persistencePort.fetch();
    }

    @Override
    public void hit(DartHitEvent event) {
        Game game = persistencePort.fetch();
        if (game != null) {
            game.hit(event.segment());
            if (game.getState() == GameState.Switch_Player) {
                uartSendPort.startButtonBlink();
            }
        }
    }

    @Override
    public void buttonPressed(ButtonPressedEvent event) {
        Game game = persistencePort.fetch();
        if (game != null) {
            game.nextPlayer();
            uartSendPort.stopButtonBlink();
        }
    }
}