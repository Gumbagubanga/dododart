package net.quombat.dododart.game.adapter.in.web;

import net.quombat.dododart.game.application.ports.in.CreateNewGameCommand;
import net.quombat.dododart.game.application.ports.in.GameUseCase;
import net.quombat.dododart.game.domain.ButtonPressedEvent;
import net.quombat.dododart.game.domain.DartHitEvent;
import net.quombat.dododart.game.domain.DartSegment;
import net.quombat.dododart.game.domain.Rules;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/x01")
@Controller
class GameController {

    private final GameUseCase gameUseCase;

    private SseEmitter sseEmitter;

    @GetMapping("/{gameType}/{noOfPlayers}")
    public String createNewGame(@PathVariable String gameType, @PathVariable int noOfPlayers) {
        Rules rules = GameType.valueOf(gameType).getRules();

        CreateNewGameCommand command = new CreateNewGameCommand(noOfPlayers, rules);
        gameUseCase.createNewGame(command);

        return "x01";
    }

    @GetMapping("/register")
    public SseEmitter registerSseEmitter() {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitter.onCompletion(() -> log.info("SseEmitter is completed"));
        sseEmitter.onTimeout(() -> log.info("SseEmitter is timed out"));
        sseEmitter.onError((ex) -> log.info("SseEmitter got error:", ex));
        this.sseEmitter = sseEmitter;

        update();
        return this.sseEmitter;
    }

    @GetMapping("/nextPlayer")
    public String nextPlayer() {
        buttonPressed(null);
        return "index";
    }

    @GetMapping("/hit/{segment}")
    public String hit(@PathVariable("segment") DartSegment segment) {
        hit(new DartHitEvent(segment));
        return "index";
    }

    @EventListener
    public void hit(DartHitEvent event) {
        gameUseCase.hit(event);
        update();
    }

    @EventListener
    public void buttonPressed(ButtonPressedEvent event) {
        gameUseCase.buttonPressed(event);
        update();
    }

    @SneakyThrows(value = IOException.class)
    void update() {
        sseEmitter.send("");
    }

}
