package net.quombat.dododart.application;

import net.quombat.dododart.application.ports.out.BoardPort;
import net.quombat.dododart.application.ports.out.GamePersistencePort;
import net.quombat.dododart.application.ports.out.RenderPort;
import net.quombat.dododart.domain.Game;
import net.quombat.dododart.domain.Player;
import net.quombat.dododart.domain.ScoreSegment;

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
        Game game = command.rules();
        int noOfPlayers = command.noOfPlayers();

        List<Player> players = IntStream.rangeClosed(1, noOfPlayers).boxed()
                .map(playerNo -> new Player(playerNo, game.startScore()))
                .collect(Collectors.toList());

        game.start(players);
        persistencePort.save(game);
        boardPort.stopButtonBlink();
        renderPort.render(List.copyOf(game.getDomainEvents()));
        game.getDomainEvents().clear();
    }

    public void hit(ScoreSegment segment) {
        Game game = persistencePort.fetch();
        if (game == null) {
            return;
        }

        game.hit(segment);
        if (game.isSwitchPlayerState()) {
            boardPort.startButtonBlink();
        }
        renderPort.render(List.copyOf(game.getDomainEvents()));
        game.getDomainEvents().clear();
    }

    public void buttonPressed() {
        Game game = persistencePort.fetch();
        if (game == null) {
            return;
        }

        game.nextPlayer();
        boardPort.stopButtonBlink();
        renderPort.render(List.copyOf(game.getDomainEvents()));
        game.getDomainEvents().clear();
    }

    public Game fetchGame() {
        return persistencePort.fetch();
    }

    public record CreateNewGameCommand(int noOfPlayers, Game rules) {
    }
}
