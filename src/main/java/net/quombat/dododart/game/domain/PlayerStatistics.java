package net.quombat.dododart.game.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerStatistics {
    private final List<Statistics> statistics = new ArrayList<>();

    public void add(int round, DartSegment segment) {
        statistics.add(new Statistics(round, segment.getScore()));
    }

    public double getPointsPerDart() {
        return statistics.stream().mapToInt(Statistics::score).average().orElse(0d);
    }

    public double getPointsPerRound() {
        return statistics.stream()
                .collect(Collectors.groupingBy(Statistics::round,
                        Collectors.summingInt(Statistics::score)))
                .values().stream()
                .mapToInt(s -> s)
                .average()
                .orElse(0d);
    }

    private record Statistics(int round, int score) {
    }
}
