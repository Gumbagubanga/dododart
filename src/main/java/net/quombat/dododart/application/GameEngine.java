package net.quombat.dododart.application;

import net.quombat.dododart.application.ports.out.BoardPort;
import net.quombat.dododart.application.ports.out.GamePersistencePort;
import net.quombat.dododart.application.ports.out.RenderPort;
import net.quombat.dododart.domain.Game;
import net.quombat.dododart.domain.ScoreSegment;

import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameEngine {

    private final GamePersistencePort persistencePort;
    private final BoardPort boardPort;
    private final RenderPort renderPort;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> schedule;

    @Getter
    private Screen activeScreen = new TitleScreen();

    public void createNewGame(CreateNewGameCommand command) {
        Game game = command.rules();
        int noOfPlayers = command.noOfPlayers();

        game.start(noOfPlayers);

        activeScreen = new GameScreen(game);
        persistencePort.save(game);
        boardPort.stopButtonBlink();
        backToTitleScreen(game);
        renderPort.render();
    }

    public void hit(ScoreSegment segment) {
        Game game = persistencePort.fetch();
        if (game == null) {
            return;
        }

        game.getDomainEvents().clear();
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

        game.getDomainEvents().clear();
        game.nextPlayer();
        boardPort.stopButtonBlink();
        backToTitleScreen(game);
        renderPort.render();
    }

    public void switchToTitle() {
        if (schedule != null && !schedule.isDone()) {
            schedule.cancel(false);
        }

        activeScreen = new TitleScreen();
        persistencePort.save(null);
        renderPort.render();
    }

    private void backToTitleScreen(Game game) {
        if (schedule != null && !schedule.isDone()) {
            schedule.cancel(false);
        }

        if (game.isGameOver()) {
            schedule = executor.schedule(this::switchToTitle, 15L, TimeUnit.SECONDS);
        } else {
            schedule = executor.schedule(this::switchToTitle, 5L, TimeUnit.MINUTES);
        }
    }

    public record CreateNewGameCommand(int noOfPlayers, Game rules) {
    }
}
