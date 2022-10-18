package net.quombat.dododart.x01.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class X01Player {

    @Getter
    @EqualsAndHashCode.Include
    private final int id;
    private final List<Statistics> statistics;

    private final List<DartSegment> hits = new ArrayList<>();
    private final int targetScore;
    private final boolean elimination;
    private int score;

    public X01Player(int id, int score, boolean elimination) {
        this.id = id;
        this.elimination = elimination;
        this.statistics = new ArrayList<>();
        this.targetScore = elimination ? score : 0;
        this.score = elimination ? 0 : score;
    }

    public void hit(int round, DartSegment segment) {
        hits.add(segment);
        statistics.add(new Statistics(round, segment.getScore()));
    }

    public void updateScore() {

        if (!isBust()) {
            score = preliminaryScore();
        }
        hits.clear();
    }

    public Optional<DartSegment> firstDart() {
        return dartThrow(1);
    }

    public Optional<DartSegment> secondDart() {
        return dartThrow(2);
    }

    public Optional<DartSegment> thirdDart() {
        return dartThrow(3);
    }

    public int dartsSum() {
        return hits.stream().map(DartSegment::getScore).reduce(0, Integer::sum);
    }

    public int getScore() {
        int preliminaryScore = preliminaryScore();
        return (isBust() ? score : preliminaryScore);
    }

    public double getPointsPerDart() {
        return statistics.stream().mapToInt(Statistics::score).average().orElse(0d);
    }

    public double getPointsPerRound() {
        return statistics.stream()
                .collect(Collectors.groupingBy(Statistics::round, Collectors.summingInt(Statistics::score)))
                .values().stream()
                .mapToInt(s -> s)
                .average()
                .orElse(0d);
    }

    public boolean isBust() {
        if (elimination) {
            return preliminaryScore() > targetScore;
        } else {
            return preliminaryScore() < 0;
        }
    }

    public boolean isWinner() {
        if (elimination) {
            return preliminaryScore() == targetScore;
        } else {
            return preliminaryScore() == 0;
        }
    }

    public boolean isTurnOver() {
        return hits.size() == 3;
    }

    private int preliminaryScore() {
        int dartsSum = dartsSum();
        if (elimination) {
            return score + dartsSum;
        } else {
            return score - dartsSum;
        }
    }

    private Optional<DartSegment> dartThrow(int throwNo) {
        return hits.stream().skip(throwNo - 1).limit(1).findFirst();
    }

    public void eliminate() {
        if (elimination) {
            score = 0;
        } else {
            throw new IllegalStateException("This method should never be called in this context");
        }
    }

    private record Statistics(int round, int score) {
    }
}
