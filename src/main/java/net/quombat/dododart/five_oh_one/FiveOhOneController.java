package net.quombat.dododart.five_oh_one;

import net.quombat.dododart.configuration.ButtonPressed;
import net.quombat.dododart.shared.domain.Dart;
import net.quombat.dododart.shared.domain.DartSegment;
import net.quombat.dododart.shared.domain.Game;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/game/501")
@RequiredArgsConstructor
class FiveOhOneController {

    private final FiveOhOneService service;
    private final ApplicationEventPublisher applicationEventPublisher;

    private SseEmitter sseEmitter;

    @GetMapping("/{noOfPlayers}")
    public String startGame(@PathVariable("noOfPlayers") int noOfPlayers, Model model) {
        service.init(noOfPlayers);
        model.addAttribute("model", FiveOhOneGameModel.create(service));
        applicationEventPublisher.publishEvent(new FiveOhOneGame());
        return "x01/501";
    }

    @GetMapping("/register")
    public SseEmitter registerSseEmitter() {
        sseEmitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitter.onCompletion(() -> log.info("SseEmitter is completed"));
        sseEmitter.onTimeout(() -> log.info("SseEmitter is timed out"));
        sseEmitter.onError((ex) -> log.info("SseEmitter got error:", ex));

        return sseEmitter;
    }

    @GetMapping("/score")
    public String score(Model model) {
        model.addAttribute("model", FiveOhOneGameModel.create(service));
        return "x01/score";
    }

    @GetMapping("/nextPlayer")
    public String nextPlayer() {
        buttonPressed(null);
        return "index";
    }

    @GetMapping("/hit/{segment}")
    public String hit(@PathVariable("segment") DartSegment segment) {
        hit(new Dart(segment));
        return "index";
    }

    @EventListener
    public void buttonPressed(ButtonPressed buttonPressed) {
        if (service.isEnabled()) {
            service.nextPlayer();
            try {
                sseEmitter.send("");
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }

    @EventListener
    public void hit(Dart event) {
        if (service.isEnabled()) {
            service.hit(event);
            try {
                sseEmitter.send("");
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }

    @EventListener
    public void init(Game game) {
        if (!(game instanceof FiveOhOneGame)) {
            service.disable();
        }
    }
}
