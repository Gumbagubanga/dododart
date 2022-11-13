package net.quombat.dododart.game.domain;

import java.util.Comparator;

public record X01Rules(int startScore, int targetScore, int maxRounds) implements Rules {

    @Override
    public String gameType() {
        return "%d".formatted(startScore);
    }

    @Override
    public boolean isBust(Game game) {
        return calculateScore(game) < targetScore;
    }

    @Override
    public boolean isWinner(Game game) {
        return calculateScore(game) == targetScore;
    }

    @Override
    public int calculateScore(Game game) {
        return game.getCurrentPlayerOldScore() - game.dartsSum();
    }

    @Override
    public Player leader(Game game) {
        return game.getPlayers().stream().min(Comparator.comparing(Player::getScore)).orElseThrow();
    }
}
