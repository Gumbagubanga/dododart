package net.quombat.dododart.game.domain;

import java.util.List;

public interface Rules {

    default Player createPlayer(int playerNo) {
        return new Player(playerNo, this);
    }

    String gameType();

    boolean isBust(Player player);

    boolean isWinner(Player player);

    int calculatePreliminaryScore(Player player);

    void hit(int round, DartSegment segment, Player currentPlayer, List<Player> players);

    int startScore();

    Player leader(List<Player> players);
}
