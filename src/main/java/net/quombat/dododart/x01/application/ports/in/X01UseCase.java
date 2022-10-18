package net.quombat.dododart.x01.application.ports.in;

import net.quombat.dododart.x01.domain.ButtonPressedEvent;
import net.quombat.dododart.x01.domain.DartHitEvent;
import net.quombat.dododart.x01.domain.X01Game;

public interface X01UseCase {
    X01Game createNewGame(CreateNewGameX01Command command);

    X01Game fetchGame();

    void hit(DartHitEvent event);

    void buttonPressed(ButtonPressedEvent buttonPressed);
}
