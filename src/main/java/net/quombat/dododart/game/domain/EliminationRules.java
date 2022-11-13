package net.quombat.dododart.game.domain;

import java.util.Comparator;
import java.util.function.Predicate;

public record EliminationRules(int startScore, int targetScore, int maxRounds) implements Rules {

    @Override
    public String gameType() {
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
        int preliminaryScore = game.getCurrentPlayerOldScore() + game.dartsSum();

        game.getPlayers().stream()
                .filter(Predicate.not(currentPlayer::equals))
                .filter(p -> p.getScore() != 0)
                .filter(p -> p.getScore() == preliminaryScore)
                .forEach(player -> player.updateScore(0));

        return preliminaryScore;
    }

    @Override
    public Player leader(Game game) {
        return game.getPlayers().stream().max(Comparator.comparing(Player::getScore)).orElseThrow();
    }
}
