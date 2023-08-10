package net.quombat.dododart.domain;

import java.util.Comparator;
import java.util.Optional;
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
        return getHits().stream().map(ScoreSegment::getPoints).reduce(0, Integer::sum);
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
