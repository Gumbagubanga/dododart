package net.quombat.dododart.five_oh_one.application;

import lombok.RequiredArgsConstructor;
import net.quombat.dododart.five_oh_one.application.port.in.FiveOhOneUseCase;
import net.quombat.dododart.five_oh_one.application.port.out.GameStatePort;
import net.quombat.dododart.five_oh_one.domain.FiveOhOnePlayer;
import net.quombat.dododart.shared.domain.DartBoardEvent;
import net.quombat.dododart.shared.domain.DartSegment;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class FiveOhOneService implements FiveOhOneUseCase {

    private final GameStatePort gameState;

    @EventListener
    public void handleDartBoardEvent(DartBoardEvent dartBoardEvent) {
        DartSegment segment = DartSegment.from(dartBoardEvent.getDart());
        HitCommand hitCommand = new HitCommand(segment, !dartBoardEvent.isHit());

        hit(hitCommand);
    }

    @Override
    public void hit(HitCommand hit) {
        FiveOhOnePlayer player = gameState.findCurrentPlayer();
        int hitCounter = gameState.increaseHitCounter(player.getId());

        boolean bust = player.preliminaryScore(hit.segment());

        if (bust) {
            System.out.println("Bust!");
            gameState.nextPlayer();
        } else {
            if (player.winner()) {
                System.out.println("Winner!");
                gameState.gameOver();
            } else {
                if (hitCounter == 3) {
                    player.acceptScore();
                    gameState.nextPlayer();
                }
            }
        }
    }
}
