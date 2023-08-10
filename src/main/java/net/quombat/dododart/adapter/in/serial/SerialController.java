package net.quombat.dododart.adapter.in.serial;

import net.quombat.dododart.application.GameEngine;

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
        gameEngine.hit(event.segment());
    }

    @EventListener
    public void buttonPressed(ButtonPressedEvent event) {
        gameEngine.buttonPressed();
    }
}
