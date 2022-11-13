package net.quombat.dododart.game.adapter.in.web;

import net.quombat.dododart.game.application.ports.in.CreateNewGameCommand;
import net.quombat.dododart.game.application.ports.in.GameUseCase;
import net.quombat.dododart.game.domain.ButtonPressedEvent;
import net.quombat.dododart.game.domain.CricketRules;
import net.quombat.dododart.game.domain.DartHitEvent;
import net.quombat.dododart.game.domain.DartSegment;
import net.quombat.dododart.game.domain.EliminationRules;
import net.quombat.dododart.game.domain.Rules;
import net.quombat.dododart.game.domain.SplitScoreRules;
import net.quombat.dododart.game.domain.X01Rules;

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

    @GetMapping("/ELIMINATION/{noOfPlayers}")
    public String createNewEliminationGame(@PathVariable int noOfPlayers) {
        Rules rules = new EliminationRules(0, 301, 10);

        CreateNewGameCommand command = new CreateNewGameCommand(noOfPlayers, rules);
        gameUseCase.createNewGame(command);

        return "x01";
    }

    @GetMapping("/FIVE_OH_ONE/{noOfPlayers}")
    public String createNewFiveOhOneGame(@PathVariable int noOfPlayers) {
        Rules rules = new X01Rules(501, 0, -1);

        CreateNewGameCommand command = new CreateNewGameCommand(noOfPlayers, rules);
        gameUseCase.createNewGame(command);

        return "x01";
    }

    @GetMapping("/SPLIT_SCORE/{noOfPlayers}")
    public String createNewSplitScoreGame(@PathVariable int noOfPlayers) {
        Rules rules = new SplitScoreRules(40);

        CreateNewGameCommand command = new CreateNewGameCommand(noOfPlayers, rules);
        gameUseCase.createNewGame(command);

        return "x01";
    }

    @GetMapping("/CRICKET/{noOfPlayers}")
    public String createNewCricketGame(@PathVariable int noOfPlayers) {
        Rules rules = new CricketRules(0);

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
