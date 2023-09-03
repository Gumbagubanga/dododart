package net.quombat.dododart.domain.rules;

import net.quombat.dododart.domain.Game;
import net.quombat.dododart.domain.Player;
import net.quombat.dododart.domain.ScoreSegment;

import java.util.Comparator;

public class HighscoreGame extends Game {

    @Override
    public String name() {
        return "Highscore";
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
        return getCurrentPlayerOldScore() + dartsSum();
    }

    private int dartsSum() {
        return getHits().stream().map(ScoreSegment::getScore).reduce(0, Integer::sum);
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
        return 7;
    }
}
