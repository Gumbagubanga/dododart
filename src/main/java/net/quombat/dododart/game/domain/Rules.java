package net.quombat.dododart.game.domain;

public interface Rules {

    String gameType();

    boolean isBust(Game game);

    boolean isWinner(Game game);

    int calculateScore(Game game);

    int startScore();

    Player leader(Game game);
}
