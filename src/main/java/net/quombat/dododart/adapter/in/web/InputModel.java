package net.quombat.dododart.adapter.in.web;

import net.quombat.dododart.domain.CricketGame;
import net.quombat.dododart.domain.EliminationGame;
import net.quombat.dododart.domain.Game;
import net.quombat.dododart.domain.MiniminationGame;
import net.quombat.dododart.domain.SplitScoreGame;
import net.quombat.dododart.domain.X01Game;

import lombok.Data;

@Data
public class InputModel {
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
