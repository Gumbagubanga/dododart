package net.quombat.dododart.adapter.in.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.quombat.dododart.application.GameEngine;
import net.quombat.dododart.infrastructure.web.SseDriver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@Controller
class GameController {

    private final GameEngine gameEngine;
    private final SseDriver sseDriver;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/register")
    public SseEmitter registerSseEmitter() {
        return sseDriver.registerSseEmitter();
    }

    @GetMapping("/render")
    public ModelAndView renderGame() {
        return gameEngine.getScreen().render();
    }
}
