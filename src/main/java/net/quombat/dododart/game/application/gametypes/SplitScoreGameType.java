package net.quombat.dododart.game.application.gametypes;

import net.quombat.dododart.game.domain.Game;
import net.quombat.dododart.game.domain.GameType;
import net.quombat.dododart.game.domain.Player;
import net.quombat.dododart.game.domain.ScoreSegment;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public record SplitScoreGameType() implements GameType {

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

        Set<ScoreSegment> dartSegments = hitOrder.get(round - 1);

        int sum = game.getHits().stream()
                .filter(dartSegments::contains)
                .map(ScoreSegment::getScore)
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
    public int startScore() {
        return 40;
    }

    @Override
    public int maxRounds() {
        return hitOrder.size();
    }

}
