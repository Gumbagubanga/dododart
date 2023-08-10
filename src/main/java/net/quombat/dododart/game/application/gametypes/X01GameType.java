package net.quombat.dododart.game.application.gametypes;

import net.quombat.dododart.game.domain.Game;
import net.quombat.dododart.game.domain.GameType;
import net.quombat.dododart.game.domain.Player;
import net.quombat.dododart.game.domain.ScoreSegment;

import java.util.Comparator;

public record X01GameType() implements GameType {

    private static final int startScore = 501;
    private static final int targetScore = 0;

    @Override
    public String name() {
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
        return game.getCurrentPlayerOldScore() - dartsSum(game);
    }

    public static int dartsSum(Game game) {
        return game.getHits().stream().map(ScoreSegment::getScore).reduce(0, Integer::sum);
    }

    @Override
    public Player leader(Game game) {
        return game.getPlayers().stream().min(Comparator.comparing(Player::getScore)).orElseThrow();
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
