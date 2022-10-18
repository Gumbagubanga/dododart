package net.quombat.dododart.x01.adapter.in.web;

import net.quombat.dododart.x01.application.ports.in.CreateNewGameX01Command;
import net.quombat.dododart.x01.application.ports.in.X01UseCase;
import net.quombat.dododart.x01.domain.ButtonPressedEvent;
import net.quombat.dododart.x01.domain.DartHitEvent;
import net.quombat.dododart.x01.domain.DartSegment;
import net.quombat.dododart.x01.domain.X01Game;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/x01")
@Controller
class X01Controller {

    private final X01UseCase x01;

    private SseEmitter sseEmitter;

    @GetMapping("/{targetScore}/{noOfPlayers}")
    public ModelAndView createNewGame(
            @PathVariable int targetScore,
            @PathVariable int noOfPlayers,
            @RequestParam Optional<Boolean> elimination,
            @RequestParam Optional<Integer> rounds) {
        CreateNewGameX01Command command = new CreateNewGameX01Command(
                noOfPlayers,
                targetScore,
                elimination.orElse(false),
                rounds.orElse(-1));
        X01Game game = x01.createNewGame(command);

        X01ViewModel viewModel = X01ViewModel.create(game);
        return new ModelAndView("x01", "model", viewModel);
    }

    @GetMapping("/score")
    public ModelAndView score() {
        X01Game game = x01.fetchGame();
        X01ViewModel viewModel = X01ViewModel.create(game);
        return new ModelAndView("fragments/x01score", "model", viewModel);
    }

    @GetMapping("/register")
    public SseEmitter registerSseEmitter() {
        sseEmitter = createSseEmitter();
        return sseEmitter;
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
        x01.hit(event);
        update();
    }

    @EventListener
    public void buttonPressed(ButtonPressedEvent event) {
        x01.buttonPressed(event);
        update();
    }


    @SneakyThrows(value = IOException.class)
    public void update() {
        sseEmitter.send("");
    }


    private static SseEmitter createSseEmitter() {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitter.onCompletion(() -> log.info("SseEmitter is completed"));
        sseEmitter.onTimeout(() -> log.info("SseEmitter is timed out"));
        sseEmitter.onError((ex) -> log.info("SseEmitter got error:", ex));
        return sseEmitter;
    }
}
