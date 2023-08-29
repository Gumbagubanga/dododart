package net.quombat.dododart.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class SplitScoreGame extends Game {

    private static final List<Set<ScoreSegment>> hitOrder = List.of(
        ScoreSegment.fifteens, ScoreSegment.sixteens, ScoreSegment.doubles,
        ScoreSegment.seventeens, ScoreSegment.eighteens, ScoreSegment.triples,
        ScoreSegment.nineteens, ScoreSegment.twenties, ScoreSegment.bulls
    );

    @Override
    public String name() {
        return "Split Score";
    }

    @Override
    public boolean isBust() {
        return false;
    }

    @Override
    public boolean isWinner() {
        return false;
    }

    @Override
    public int calculateScore() {
        int round = getRound();

        Set<ScoreSegment> dartSegments = hitOrder.get(round - 1);

        int sum = getHits().stream()
            .filter(dartSegments::contains)
            .map(ScoreSegment::getScore)
            .reduce(0, Integer::sum);

        int score = getCurrentPlayerOldScore();
        if (isTurnOver()) {
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
    public Player leader() {
        return getPlayers().stream().max(Comparator.comparing(Player::getScore)).orElseThrow();
    }

    @Override
    public int startScore() {
        return 40;
    }

    @Override
    public int maxRounds() {
        return hitOrder.size();
    }
}
