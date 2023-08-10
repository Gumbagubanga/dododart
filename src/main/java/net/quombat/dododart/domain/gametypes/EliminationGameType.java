package net.quombat.dododart.domain.gametypes;

import net.quombat.dododart.domain.Game;
import net.quombat.dododart.domain.GameType;
import net.quombat.dododart.domain.Player;
import net.quombat.dododart.domain.ScoreSegment;

import java.util.Comparator;
import java.util.function.Predicate;

public record EliminationGameType() implements GameType {

    private static final int targetScore = 301;

    @Override
    public String name() {
        return "%d Elimination".formatted(targetScore);
    }

    @Override
    public boolean isBust(Game game) {
        return calculateScore(game) > targetScore;
    }

    @Override
    public boolean isWinner(Game game) {
        return calculateScore(game) == targetScore;
    }

    @Override
    public int calculateScore(Game game) {
        Player currentPlayer = game.determineCurrentPlayer();
        int preliminaryScore = game.getCurrentPlayerOldScore() + dartsSum(game);

        game.getPlayers().stream()
                .filter(Predicate.not(currentPlayer::equals))
                .filter(p -> p.getScore() != 0)
                .filter(p -> p.getScore() == preliminaryScore)
                .forEach(player -> player.updateScore(0));

        return preliminaryScore;
    }

    public static int dartsSum(Game game) {
        return game.getHits().stream().map(ScoreSegment::getScore).reduce(0, Integer::sum);
    }

    @Override
    public Player leader(Game game) {
        return game.getPlayers().stream().max(Comparator.comparing(Player::getScore)).orElseThrow();
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
