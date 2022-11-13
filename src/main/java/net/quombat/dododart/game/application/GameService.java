package net.quombat.dododart.game.application;

import net.quombat.dododart.game.application.ports.in.CreateNewGameCommand;
import net.quombat.dododart.game.application.ports.in.GameUseCase;
import net.quombat.dododart.game.application.ports.out.BoardPort;
import net.quombat.dododart.game.application.ports.out.GamePersistencePort;
import net.quombat.dododart.game.domain.ButtonPressedEvent;
import net.quombat.dododart.game.domain.DartHitEvent;
import net.quombat.dododart.game.domain.Game;
import net.quombat.dododart.game.domain.Rules;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class GameService implements GameUseCase {

    private final GamePersistencePort persistencePort;
    private final BoardPort boardPort;

    @Override
    public void createNewGame(CreateNewGameCommand command) {
        int noOfPlayers = command.noOfPlayers();
        Rules rules = command.rules();

        Game game = new Game(noOfPlayers, rules);
        persistencePort.save(game);
    }

    @Override
    public void hit(DartHitEvent event) {
        Game game = persistencePort.fetch();
        if (game != null) {
            game.hit(event.segment());
            if (game.isSwitchPlayerState()) {
                boardPort.startButtonBlink();
            }
        }
    }

    @Override
    public void buttonPressed(ButtonPressedEvent event) {
        Game game = persistencePort.fetch();
        if (game != null) {
            game.nextPlayer();
            boardPort.stopButtonBlink();
        }
    }
}
