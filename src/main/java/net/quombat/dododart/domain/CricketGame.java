package net.quombat.dododart.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CricketGame extends Game {

    private static final Set<Integer> ALL_VALID_SEGMENTS = ScoreSegment.highs.stream()
        .map(ScoreSegment::getPoints)
        .collect(Collectors.toSet());

    @Override
    public String name() {
        return "Cricket";
    }

    @Override
    public boolean isBust() {
        return false;
    }

    @Override
    public boolean isWinner() {
        Player currentPlayer = this.determineCurrentPlayer();
        Map<Integer, Integer> hitDistributionPerSlice = currentPlayer.getStatistics()
            .getHitDistributionPerSlice();

        boolean allSegmentsClosed = ALL_VALID_SEGMENTS.stream()
            .map(p -> hitDistributionPerSlice.getOrDefault(p, 0) >= 3)
            .reduce(Boolean::logicalAnd)
            .orElse(false);

        boolean isLeader = currentPlayer.equals(leader());

        return allSegmentsClosed && isLeader;
    }

    @Override
    public int calculateScore() {
        return getCurrentScore() + points();
    }

    private int points() {
        Game game = this;
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
    public Player leader() {
        return getPlayers().stream().max(Comparator.comparing(Player::getScore)).orElseThrow();
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
