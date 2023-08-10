package net.quombat.dododart.game.adapter.in.serial;

import net.quombat.dododart.game.application.GameEngine;
import net.quombat.dododart.game.domain.ButtonPressedEvent;
import net.quombat.dododart.game.domain.DartHitEvent;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
class SerialController {

    private final GameEngine gameEngine;

    @EventListener
    public void hit(DartHitEvent event) {
        gameEngine.hit(event);
    }

    @EventListener
    public void buttonPressed(ButtonPressedEvent event) {
        gameEngine.buttonPressed(event);
    }
}
