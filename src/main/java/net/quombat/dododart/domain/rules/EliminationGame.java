package net.quombat.dododart.domain.rules;

import net.quombat.dododart.domain.Game;
import net.quombat.dododart.domain.Player;
import net.quombat.dododart.domain.ScoreSegment;
import net.quombat.dododart.domain.events.PlayerEliminatedEvent;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class EliminationGame extends Game {

    private static final int targetScore = 301;

    @Override
    public String name() {
        return "%d Elimination".formatted(targetScore);
    }

    @Override
    public boolean isBust() {
        return calculateScore() > targetScore;
    }

    @Override
    public boolean isWinner() {
        return calculateScore() == targetScore;
    }

    @Override
    public int calculateScore() {
        Player currentPlayer = determineCurrentPlayer();
        int preliminaryScore = getCurrentPlayerOldScore() + dartsSum();

        checkElimination(currentPlayer, preliminaryScore);

        return preliminaryScore;
    }

    private void checkElimination(Player currentPlayer, int preliminaryScore) {
        Optional<Player> otherPlayerWithSameScore = getPlayers().stream()
            .filter(Predicate.not(currentPlayer::equals))
            .filter(p -> p.getScore() != 0)
            .filter(p -> p.getScore() == preliminaryScore)
            .findAny();

        otherPlayerWithSameScore.ifPresent(p -> elimination(p, currentPlayer));
    }

    private void elimination(Player eliminated, Player eliminator) {
        eliminated.updateScore(startScore());
        registerEvent(new PlayerEliminatedEvent(eliminated, eliminator));
    }

    private int dartsSum() {
        return getHits().stream().map(ScoreSegment::getScore).reduce(0, Integer::sum);
    }

    @Override
    public Player leader() {
        return getPlayers().stream().max(Comparator.comparing(Player::getScore)).orElseThrow();
    }

    @Override
    public int startScore() {
        return 0;
    }

    @Override
    public int maxRounds() {
        return 10;
    }

}
