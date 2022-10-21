package net.quombat.dododart.game.domain;

import java.util.Comparator;
import java.util.List;

public record X01Rules(int startScore, int targetScore) implements Rules {

    @Override
    public String gameType() {
        return "%d".formatted(startScore);
    }

    @Override
    public boolean isBust(Player player) {
        return player.preliminaryScore() < targetScore;
    }

    @Override
    public boolean isWinner(Player player) {
        return player.preliminaryScore() == targetScore;
    }

    @Override
    public int calculatePreliminaryScore(Player player) {
        return player.getScore() - player.dartsSum();
    }

    @Override
    public void hit(int round, DartSegment segment, Player currentPlayer, List<Player> players) {
        currentPlayer.hit(round, segment);
    }

    @Override
    public Player leader(List<Player> players) {
        return players.stream().min(Comparator.comparing(Player::currentScore)).orElseThrow();
    }
}
