package net.quombat.dododart.five_oh_one;

import net.quombat.dododart.shared.domain.DartSegment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class FiveOhOnePlayer {

    private final int id;

    private int score;
    private List<Statistics> statistics;

    public FiveOhOnePlayer(int id, int score) {
        this.id = id;
        this.score = score;
        this.statistics = new ArrayList<>();
    }

    public void addDart(int round, DartSegment segment) {
        statistics.add(new Statistics(round, segment.getScore()));
    }

    public void updateScore(int score) {
        this.score = score;
    }

    public double getPointsPerDart() {
        return statistics.stream().mapToInt(Statistics::score).average().orElse(0d);
    }

    public double getPointsPerRound() {
        Collection<Integer> sumPerRound = statistics.stream()
                .collect(Collectors.groupingBy(Statistics::round, Collectors.summingInt(Statistics::score)))
                .values();
        return sumPerRound.stream().mapToInt(s -> s).average().orElse(0d);
    }

    private record Statistics(int round, int score) {
    }
}
