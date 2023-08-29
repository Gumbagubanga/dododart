package net.quombat.dododart.adapter.in.web;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.quombat.dododart.application.GameEngine;
import net.quombat.dododart.domain.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RequiredArgsConstructor
@Controller
class MenuController {

    private final GameEngine gameEngine;

    @GetMapping("/menu")
    public String menu(Model model) {
        model.addAttribute("inputModel", new InputModel());
        return "menu";
    }

    @ResponseBody
    @PostMapping(headers = "HX-Request", produces = MediaType.APPLICATION_JSON_VALUE)
    public void createNewGame(@ModelAttribute InputModel inputModel) {
        GameEngine.CreateNewGameCommand command = new GameEngine.CreateNewGameCommand(inputModel.getNoOfPlayers(), inputModel.getRules());
        gameEngine.createNewGame(command);
    }

    @Data
    public static class InputModel {
        private int noOfPlayers = 2;
        private String gameType;

        Game getRules() {
            return getRules(gameType);
        }

        private static Game getRules(String gameType) {
            return switch (gameType.toLowerCase()) {
                case "cricket" -> new CricketGame();
                case "elimination" -> new EliminationGame();
                case "minimination" -> new MiniminationGame();
                case "splitscore" -> new SplitScoreGame();
                default -> new X01Game();
            };
        }
    }
}
