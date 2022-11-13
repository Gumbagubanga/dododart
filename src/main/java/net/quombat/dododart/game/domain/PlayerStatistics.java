package net.quombat.dododart.game.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerStatistics {
    private final List<Hits> statistics = new ArrayList<>();

    public void add(int round, DartSegment segment) {
        statistics.add(new Hits(round, segment));
    }

    public double getPointsPerDart() {
        return statistics.stream()
                .map(Hits::segment)
                .mapToInt(DartSegment::getScore)
                .average()
                .orElse(0d);
    }

    public double getPointsPerRound() {
        return statistics.stream()
                .collect(Collectors.groupingBy(Hits::round,
                        Collectors.summingInt(e -> e.segment.getScore())))
                .values().stream()
                .mapToInt(s -> s)
                .average()
                .orElse(0d);
    }

    public Map<Integer, Integer> getHitDistributionPerSlice() {
        return statistics.stream()
                .map(PlayerStatistics.Hits::segment)
                .collect(Collectors.groupingBy(DartSegment::getPoints,
                        Collectors.summingInt(DartSegment::getMultiplier)));
    }

    public record Hits(int round, DartSegment segment) {
    }

}
