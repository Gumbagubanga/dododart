package net.quombat.dododart.domain;

import java.util.Comparator;
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

        getPlayers().stream()
                .filter(Predicate.not(currentPlayer::equals))
                .filter(p -> p.getScore() != 0)
                .filter(p -> p.getScore() == preliminaryScore)
                .forEach(player -> player.updateScore(0));

        return preliminaryScore;
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
