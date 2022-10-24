package net.quombat.dododart.game.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Player {

    @EqualsAndHashCode.Include
    private final int id;
    private final PlayerStatistics statistics = new PlayerStatistics();

    private int score;

    public Player(int id, int score) {
        this.id = id;
        this.score = score;
    }

    public void hit(int round, DartSegment segment) {
        statistics.add(round, segment);
    }

    public void updateScore(int score) {
        this.score = score;
    }

}
