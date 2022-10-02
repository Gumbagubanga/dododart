package net.quombat.dododart.five_oh_one.application.port.out;

import net.quombat.dododart.five_oh_one.domain.FiveOhOnePlayer;

public interface GameStatePort {

    FiveOhOnePlayer findCurrentPlayer();

    int increaseHitCounter(int playerId);

    void nextPlayer();

    void gameOver();

}
