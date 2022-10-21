package net.quombat.dododart.game.application.ports.in;

import net.quombat.dododart.game.domain.ButtonPressedEvent;
import net.quombat.dododart.game.domain.DartHitEvent;
import net.quombat.dododart.game.domain.Game;

public interface GameUseCase {
    Game createNewGame(CreateNewGameCommand command);

    Game fetchGame();

    void hit(DartHitEvent event);

    void buttonPressed(ButtonPressedEvent buttonPressed);
}
