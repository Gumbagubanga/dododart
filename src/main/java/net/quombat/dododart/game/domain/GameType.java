package net.quombat.dododart.game.domain;

public interface GameType {

    String name();

    boolean isBust(Game game);

    boolean isWinner(Game game);

    int calculateScore(Game game);

    int startScore();

    Player leader(Game game);

    int maxRounds();

    default int throwsPerTurn() {
        return 3;
    }
}
