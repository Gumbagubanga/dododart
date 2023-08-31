package net.quombat.dododart.adapter.in.web;

import net.quombat.dododart.application.GameEngine;
import net.quombat.dododart.application.GameEngine.CreateNewGameCommand;
import net.quombat.dododart.domain.Game;
import net.quombat.dododart.domain.rules.CricketGame;
import net.quombat.dododart.domain.rules.EliminationGame;
import net.quombat.dododart.domain.rules.MiniminationGame;
import net.quombat.dododart.domain.rules.SplitScoreGame;
import net.quombat.dododart.domain.rules.X01Game;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        CreateNewGameCommand command = new CreateNewGameCommand(inputModel.getNoOfPlayers(), inputModel.getRules());
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
