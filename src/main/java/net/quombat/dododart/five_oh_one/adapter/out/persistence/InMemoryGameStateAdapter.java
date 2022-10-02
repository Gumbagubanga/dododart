package net.quombat.dododart.five_oh_one.adapter.out.persistence;

import net.quombat.dododart.five_oh_one.application.port.out.GameStatePort;
import net.quombat.dododart.five_oh_one.domain.FiveOhOnePlayer;
import org.springframework.stereotype.Component;

@Component
class InMemoryGameStateAdapter implements GameStatePort {
    @Override
    public FiveOhOnePlayer findPlayer(int playerId, int gameId) {
        return null;
    }

    @Override
    public int increaseHitCounter(int playerId) {
        return 0;
    }

    @Override
    public void nextPlayer() {

    }

    @Override
    public void gameOver() {

    }
}
