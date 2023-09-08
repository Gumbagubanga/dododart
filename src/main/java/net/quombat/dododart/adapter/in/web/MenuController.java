package net.quombat.dododart.adapter.in.web;

import net.quombat.dododart.application.GameEngine;
import net.quombat.dododart.application.GameEngine.CreateNewGameCommand;
import net.quombat.dododart.domain.Game;
import net.quombat.dododart.domain.rules.CricketGame;
import net.quombat.dododart.domain.rules.EliminationGame;
import net.quombat.dododart.domain.rules.HighscoreGame;
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

import java.util.List;
import java.util.function.Supplier;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
class MenuController {

    private final GameEngine gameEngine;

    @GetMapping("/")
    public String menu(Model model) {
        model.addAttribute("inputModel", new InputModel());
        return "menu";
    }

    @ResponseBody
    @PostMapping(path = "/title", headers = "HX-Request", produces = MediaType.APPLICATION_JSON_VALUE)
    public void title() {
        gameEngine.switchToTitle();
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
        private final List<GameType> games = List.of(
            new GameType("501", "x01", X01Game::new),
            new GameType("301 Elimination", "elimination", EliminationGame::new),
            new GameType("101 Minimination", "minimination", MiniminationGame::new),
            new GameType("Highscore", "highscore", HighscoreGame::new),
            new GameType("Split Score", "splitscore", SplitScoreGame::new),
            new GameType("Cricket", "cricket", CricketGame::new)
        );

        Game getRules() {
            return games.stream()
                .filter(g -> g.value.equals(gameType))
                .findFirst()
                .map(GameType::gameNew)
                .map(Supplier::get)
                .orElseThrow();
        }

        public record GameType(String name, String value, Supplier<Game> gameNew) {
        }
    }
}
