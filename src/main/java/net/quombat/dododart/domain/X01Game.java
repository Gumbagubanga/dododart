package net.quombat.dododart.domain;

import java.util.Comparator;

public class X01Game extends Game {

    private static final int startScore = 501;
    private static final int targetScore = 0;

    @Override
    public String name() {
        return "%d".formatted(startScore);
    }

    @Override
    public boolean isBust() {
        return calculateScore() < targetScore;
    }

    @Override
    public boolean isWinner() {
        return calculateScore() == targetScore;
    }

    @Override
    public int calculateScore() {
        return getCurrentPlayerOldScore() - dartsSum();
    }

    private int dartsSum() {
        return getHits().stream().map(ScoreSegment::getScore).reduce(0, Integer::sum);
    }

    @Override
    public Player leader() {
        return getPlayers().stream().min(Comparator.comparing(Player::getScore)).orElseThrow();
    }

    @Override
    public int startScore() {
        return startScore;
    }

    @Override
    public int maxRounds() {
        return -1;
    }
}
