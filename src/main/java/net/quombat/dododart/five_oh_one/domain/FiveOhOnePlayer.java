package net.quombat.dododart.five_oh_one.domain;

import lombok.Getter;
import net.quombat.dododart.shared.domain.DartSegment;

@Getter
public class FiveOhOnePlayer {

    private final int id;

    private int score;
    private int preliminaryScore;

    public FiveOhOnePlayer() {
        this(1, 501);
    }

    public FiveOhOnePlayer(int score) {
        this(1, score);
    }

    public FiveOhOnePlayer(int id, int score) {
        this.id = id;
        this.score = score;
    }

    public boolean preliminaryScore(DartSegment segment) {
        preliminaryScore = score - segment.getScore();

        if (preliminaryScore == 0) {
            acceptScore();
        }

        return isBust();
    }

    public boolean acceptScore() {
        boolean bust = isBust();
        if (!bust) {
            score = preliminaryScore;
        }
        return bust;
    }

    private boolean isBust() {
        return preliminaryScore < 0;
    }

    public boolean winner() {
        return score == 0;
    }

}
