package net.quombat.dododart.game.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public record SplitScoreRules(int startScore) implements Rules {

    private static final List<Set<DartSegment>> hitOrder = List.of(
            DartSegment.fifteens, DartSegment.sixteens, DartSegment.doubles,
            DartSegment.seventeens, DartSegment.eighteens, DartSegment.triples,
            DartSegment.nineteens, DartSegment.twenties, DartSegment.bulls
    );

    @Override
    public String gameType() {
        return "Split Score";
    }

    @Override
    public boolean isBust(Game game) {
        return false;
    }

    @Override
    public boolean isWinner(Game game) {
        return false;
    }

    @Override
    public int calculateScore(Game game) {
        int round = game.getRound();

        Set<DartSegment> dartSegments = hitOrder.get(round - 1);

        int sum = Stream.of(game.firstDart(), game.secondDart(), game.thirdDart())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(dartSegments::contains)
                .map(DartSegment::getScore)
                .reduce(0, Integer::sum);

        int score = game.getCurrentPlayerOldScore();
        if (game.isTurnOver()) {
            if (sum == 0) {
                return score / 2;
            } else {
                return score + sum;
            }
        } else {
            return score + sum;
        }
    }

    @Override
    public Player leader(Game game) {
        return game.getPlayers().stream().max(Comparator.comparing(Player::getScore)).orElseThrow();
    }

    @Override
    public int maxRounds() {
        return hitOrder.size();
    }

}
