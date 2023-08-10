package net.quombat.dododart.game.application.gametypes;

import net.quombat.dododart.game.domain.Game;
import net.quombat.dododart.game.domain.GameType;
import net.quombat.dododart.game.domain.Player;
import net.quombat.dododart.game.domain.ScoreSegment;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record CricketGameType() implements GameType {

    private static final Set<Integer> ALL_VALID_SEGMENTS = ScoreSegment.highs.stream()
            .map(ScoreSegment::getPoints)
            .collect(Collectors.toSet());

    @Override
    public String name() {
        return "Cricket";
    }

    @Override
    public boolean isBust(Game game) {
        return false;
    }

    @Override
    public boolean isWinner(Game game) {
        Player currentPlayer = game.determineCurrentPlayer();
        Map<Integer, Integer> hitDistributionPerSlice = currentPlayer.getStatistics()
                .getHitDistributionPerSlice();

        boolean allSegmentsClosed = ALL_VALID_SEGMENTS.stream()
                .map(p -> hitDistributionPerSlice.getOrDefault(p, 0) >= 3)
                .reduce(Boolean::logicalAnd)
                .orElse(false);

        boolean isLeader = currentPlayer.equals(leader(game));

        return allSegmentsClosed && isLeader;
    }

    @Override
    public int calculateScore(Game game) {
        return game.getCurrentScore() + points(game);
    }

    private static int points(Game game) {
        ScoreSegment lastDart = game.lastDart();
        int points = lastDart.getPoints();

        if (!ALL_VALID_SEGMENTS.contains(points)) {
            return 0;
        }

        int hitsPerSlice = game.determineCurrentPlayer().getStatistics()
                .getHitDistributionPerSlice().getOrDefault(points, 0);

        if (hitsPerSlice <= 3) {
            return 0;
        }

        List<Player> players = game.getPlayers();
        boolean sliceOpen = players.stream()
                .filter(Predicate.not(game.determineCurrentPlayer()::equals))
                .map(Player::getStatistics)
                .map(Player.Statistics::getHitDistributionPerSlice)
                .map(h -> h.getOrDefault(points, 0) < 3)
                .reduce(Boolean::logicalAnd)
                .orElse(false);

        if (!sliceOpen) {
            return 0;
        }

        return points * multiplier(hitsPerSlice, lastDart.getMultiplier());
    }

    private static int multiplier(int hitsPerSlice, int multiplier) {
        return switch (hitsPerSlice) {
            case 4 -> Math.min(multiplier, 1);
            case 5 -> Math.min(multiplier, 2);
            default -> multiplier;
        };
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
        return 25;
    }
}
