package net.quombat.dododart.game.domain;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public record EliminationRules(int startScore, int targetScore) implements Rules {

    @Override
    public String gameType() {
        return "%d Elimination".formatted(targetScore);
    }

    @Override
    public boolean isBust(Player player) {
        return player.preliminaryScore() > targetScore;
    }

    @Override
    public boolean isWinner(Player player) {
        return player.preliminaryScore() == targetScore;
    }

    @Override
    public int calculatePreliminaryScore(Player player) {
        return player.getScore() + player.dartsSum();
    }

    @Override
    public void hit(int round, DartSegment segment, Player currentPlayer, List<Player> players) {
        currentPlayer.hit(round, segment);
        players.stream()
                .filter(Predicate.not(currentPlayer::equals))
                .filter(p -> p.currentScore() != 0)
                .filter(p -> p.currentScore() == currentPlayer.currentScore())
                .forEach(player -> player.resetScore());
    }

    @Override
    public Player leader(List<Player> players) {
        return players.stream().max(Comparator.comparing(Player::currentScore)).orElseThrow();
    }
}
