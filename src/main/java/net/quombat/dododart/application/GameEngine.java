package net.quombat.dododart.application;

import net.quombat.dododart.application.ports.out.BoardPort;
import net.quombat.dododart.application.ports.out.GamePersistencePort;
import net.quombat.dododart.application.ports.out.RenderPort;
import net.quombat.dododart.domain.Game;
import net.quombat.dododart.domain.Player;
import net.quombat.dododart.domain.ScoreSegment;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameEngine {

    private final GamePersistencePort persistencePort;
    private final BoardPort boardPort;
    private final RenderPort renderPort;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @Getter
    private Screen activeScreen = new TitleScreen();

    public void createNewGame(CreateNewGameCommand command) {
        Game game = command.rules();
        int noOfPlayers = command.noOfPlayers();

        List<Player> players = IntStream.rangeClosed(1, noOfPlayers).boxed()
            .map(playerNo -> new Player(playerNo, game.startScore()))
            .collect(Collectors.toList());

        game.start(players);

        activeScreen = new GameScreen(game);
        persistencePort.save(game);
        boardPort.stopButtonBlink();
        renderPort.render();
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
        backToTitleScreen(game);
        renderPort.render();
    }

    public void buttonPressed() {
        Game game = persistencePort.fetch();
        if (game == null) {
            return;
        }

        game.nextPlayer();
        boardPort.stopButtonBlink();
        backToTitleScreen(game);
        renderPort.render();
    }

    private void backToTitleScreen(Game game) {
        if (game.isGameOver()) {
            executor.schedule(() -> {
                activeScreen = new TitleScreen();
                persistencePort.save(null);
                renderPort.render();
            }, 10L, TimeUnit.SECONDS);
        }
    }

    public record CreateNewGameCommand(int noOfPlayers, Game rules) {
    }
}
