package net.quombat.dododart.game.application.ports.in;

import net.quombat.dododart.game.domain.ButtonPressedEvent;
import net.quombat.dododart.game.domain.DartHitEvent;

public interface GameUseCase {
    void createNewGame(CreateNewGameCommand command);

    void hit(DartHitEvent event);

    void buttonPressed(ButtonPressedEvent buttonPressed);
}
