package net.quombat.dododart.game.application;

import net.quombat.dododart.game.adapter.out.persistence.TestMemoryAdapter;
import net.quombat.dododart.game.application.gametypes.CricketGameType;
import net.quombat.dododart.game.application.ports.out.BoardPort;
import net.quombat.dododart.game.application.ports.out.GamePersistencePort;
import net.quombat.dododart.game.domain.ButtonPressedEvent;
import net.quombat.dododart.game.domain.DartHitEvent;
import net.quombat.dododart.game.domain.ScoreSegment;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import java.util.stream.Stream;

@Testable
class CricketRulesTest {

    private GamePersistencePort persistence;
    private GameEngine gameEngine;

    @BeforeEach
    void setup() {
        persistence = TestMemoryAdapter.create();
        gameEngine = createGameEngine(persistence);
    }

    @Test
    void cricketTest() {
        GameEngine.CreateNewGameCommand command = new GameEngine.CreateNewGameCommand(2, new CricketGameType());
        gameEngine.createNewGame(command);

        throwDarts(ScoreSegment.SINGLE_01, ScoreSegment.SINGLE_01, ScoreSegment.SINGLE_01, 0);
        throwDarts(ScoreSegment.TRIPLE_15, ScoreSegment.SINGLE_01, ScoreSegment.SINGLE_01, 0);

        throwDarts(ScoreSegment.TRIPLE_15, ScoreSegment.TRIPLE_15, ScoreSegment.SINGLE_01, 0);
        throwDarts(ScoreSegment.DOUBLE_16, ScoreSegment.DOUBLE_16, ScoreSegment.SINGLE_01, 16);

        throwDarts(ScoreSegment.TRIPLE_17, ScoreSegment.TRIPLE_17, ScoreSegment.TRIPLE_16, 51);
        throwDarts(ScoreSegment.DOUBLE_16, ScoreSegment.DOUBLE_16, ScoreSegment.SINGLE_01, 16);
    }

    private void throwDarts(ScoreSegment firstDart, ScoreSegment secondDart, ScoreSegment thirdDart, int expectedScore) {
        Stream.of(firstDart, secondDart, thirdDart).map(DartHitEvent::new).forEach(gameEngine::hit);
        Assertions.assertThat(persistence.fetch().getCurrentScore()).isEqualTo(expectedScore);
        pressButton();
    }

    private void pressButton() {
        gameEngine.buttonPressed(new ButtonPressedEvent());
    }

    private static GameEngine createGameEngine(GamePersistencePort persistencePort) {
        return new GameEngine(persistencePort, new BoardPort() {
            @Override
            public void startButtonBlink() {
            }

            @Override
            public void stopButtonBlink() {
            }
        }, () -> {
        });
    }
}