package net.quombat.dododart.game.application;

import net.quombat.dododart.game.application.ports.out.BoardPort;
import net.quombat.dododart.game.application.ports.out.GamePersistencePort;
import net.quombat.dododart.game.application.ports.out.RenderPort;
import net.quombat.dododart.game.domain.ButtonPressedEvent;
import net.quombat.dododart.game.domain.DartHitEvent;
import net.quombat.dododart.game.domain.Game;
import net.quombat.dododart.game.domain.GameType;
import net.quombat.dododart.game.domain.Player;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameEngine {

    private final GamePersistencePort persistencePort;
    private final BoardPort boardPort;
    private final RenderPort renderPort;

    public void createNewGame(CreateNewGameCommand command) {
        int noOfPlayers = command.noOfPlayers();
        GameType rules = command.rules();

        List<Player> players = IntStream.rangeClosed(1, noOfPlayers).boxed()
                .map(playerNo -> new Player(playerNo, rules.startScore()))
                .collect(Collectors.toList());
        Game game = new Game(rules, players);

        persistencePort.save(game);
        boardPort.stopButtonBlink();
        renderPort.render();
    }

    public void hit(DartHitEvent event) {
        Game game = persistencePort.fetch();
        if (game == null) {
            return;
        }

        game.hit(event.segment());
        if (game.isSwitchPlayerState()) {
            boardPort.startButtonBlink();
        }
        renderPort.render();
    }

    public void buttonPressed(ButtonPressedEvent event) {
        Game game = persistencePort.fetch();
        if (game == null) {
            return;
        }

        game.nextPlayer();
        boardPort.stopButtonBlink();
        renderPort.render();
    }

    public Game fetchGame() {
        return persistencePort.fetch();
    }

    public record CreateNewGameCommand(int noOfPlayers, GameType rules) {
    }
}
