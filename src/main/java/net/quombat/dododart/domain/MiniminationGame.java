package net.quombat.dododart.domain;

import java.util.Comparator;
import java.util.function.Predicate;

public class MiniminationGame extends Game {

    private static final int targetScore = 101;

    @Override
    public String name() {
        return "%d Minimination".formatted(targetScore);
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
        return getHits().stream().map(ScoreSegment::getPoints).reduce(0, Integer::sum);
    }

    @Override
    public Player leader() {
        return this.getPlayers().stream().max(Comparator.comparing(Player::getScore)).orElseThrow();
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
