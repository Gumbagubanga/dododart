package net.quombat.dododart.game.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Player {

    private final List<Statistics> statistics = new ArrayList<>();
    private final List<DartSegment> hits = new ArrayList<>();

    @Getter
    @EqualsAndHashCode.Include
    private final int id;
    private final Rules rules;

    @Getter
    private int score;

    public Player(int id, Rules rules) {
        this.id = id;
        this.rules = rules;
        this.score = rules.startScore();
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

    public int currentScore() {
        int preliminaryScore = preliminaryScore();
        return (isBust() ? score : preliminaryScore);
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

    public boolean isBust() {
        return rules.isBust(this);
    }

    public boolean isWinner() {
        return rules.isWinner(this);
    }

    public boolean isTurnOver() {
        return hits.size() == 3;
    }

    public int preliminaryScore() {
        return rules.calculatePreliminaryScore(this);
    }

    private Optional<DartSegment> dartThrow(int throwNo) {
        return hits.stream().skip(throwNo - 1).limit(1).findFirst();
    }

    public void resetScore() {
        score = rules.startScore();
    }

    private record Statistics(int round, int score) {
    }
}
