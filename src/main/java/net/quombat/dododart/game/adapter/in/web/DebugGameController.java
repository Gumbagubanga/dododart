package net.quombat.dododart.game.adapter.in.web;

import net.quombat.dododart.game.application.GameEngine;
import net.quombat.dododart.game.domain.ButtonPressedEvent;
import net.quombat.dododart.game.domain.DartHitEvent;
import net.quombat.dododart.game.domain.ScoreSegment;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@Profile("debug")
class DebugGameController {

    private final GameEngine gameEngine;

    @GetMapping("/debug")
    public String debug() {
        return "debug";
    }

    @PostMapping("/nextPlayer")
    public String nextPlayer() {
        gameEngine.buttonPressed(new ButtonPressedEvent());
        return "debug";
    }

    @PostMapping("/hit/{segment}")
    public String hit(@PathVariable("segment") ScoreSegment segment) {
        gameEngine.hit(new DartHitEvent(segment));
        return "debug";
    }
}
