package net.quombat.dododart.game.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Player {

    @EqualsAndHashCode.Include
    private final int id;
    private final Statistics statistics = new Statistics();

    private int score;

    public Player(int id, int score) {
        this.id = id;
        this.score = score;
    }

    public void hit(int round, ScoreSegment segment) {
        statistics.add(round, segment);
    }

    public void updateScore(int score) {
        this.score = score;
    }

    public static class Statistics {
        private final List<Hits> statistics = new ArrayList<>();

        public void add(int round, ScoreSegment segment) {
            statistics.add(new Hits(round, segment));
        }

        public double getPointsPerDart() {
            return statistics.stream()
                    .map(Hits::segment)
                    .mapToInt(ScoreSegment::getScore)
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
                    .map(Hits::segment)
                    .collect(Collectors.groupingBy(ScoreSegment::getPoints,
                            Collectors.summingInt(ScoreSegment::getMultiplier)));
        }

        private record Hits(int round, ScoreSegment segment) {
        }

    }
}
