package net.quombat.dododart.adapter.in.web;

import net.quombat.dododart.application.GameEngine;
import net.quombat.dododart.domain.ScoreSegment;

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
        gameEngine.buttonPressed();
        return "debug";
    }

    @PostMapping("/hit/{segment}")
    public String hit(@PathVariable ScoreSegment segment) {
        gameEngine.hit(segment);
        return "debug";
    }
}
