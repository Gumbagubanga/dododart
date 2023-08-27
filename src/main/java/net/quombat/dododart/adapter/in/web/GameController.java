package net.quombat.dododart.adapter.in.web;

import net.quombat.dododart.application.GameEngine;
import net.quombat.dododart.domain.Game;
import net.quombat.dododart.infrastructure.web.SseDriver;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static net.quombat.dododart.application.GameEngine.CreateNewGameCommand;

@Slf4j
@RequiredArgsConstructor
@Controller
class GameController {

    private final GameEngine gameEngine;
    private final SseDriver sseDriver;

    @GetMapping("/register")
    public SseEmitter registerSseEmitter() {
        return sseDriver.registerSseEmitter();
    }

    @ResponseBody
    @PostMapping(headers = "HX-Request", produces = MediaType.APPLICATION_JSON_VALUE)
    public void createNewGame(@ModelAttribute InputModel inputModel) {
        CreateNewGameCommand command = new CreateNewGameCommand(inputModel.getNoOfPlayers(), inputModel.getRules());
        gameEngine.createNewGame(command);
    }

    @GetMapping("/board")
    public String board() {
        return "board";
    }

    @GetMapping("/game")
    public ModelAndView renderGame() {
        Game game = gameEngine.fetchGame();
        GameViewModel viewModel = GameViewModel.create(game);
        return new ModelAndView("fragments/game", "model", viewModel);
    }

}
