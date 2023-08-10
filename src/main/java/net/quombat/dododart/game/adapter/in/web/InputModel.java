package net.quombat.dododart.game.adapter.in.web;

import net.quombat.dododart.game.application.gametypes.CricketGameType;
import net.quombat.dododart.game.application.gametypes.EliminationGameType;
import net.quombat.dododart.game.application.gametypes.MiniminationGameType;
import net.quombat.dododart.game.application.gametypes.SplitScoreGameType;
import net.quombat.dododart.game.application.gametypes.X01GameType;
import net.quombat.dododart.game.domain.GameType;

import lombok.Data;

@Data
public class InputModel {
    private int noOfPlayers = 2;
    private String gameType;

    GameType getRules() {
        return getRules(gameType);
    }

    private static GameType getRules(String gameType) {
        return switch (gameType.toLowerCase()) {
            case "cricket" -> new CricketGameType();
            case "elimination" -> new EliminationGameType();
            case "minimination" -> new MiniminationGameType();
            case "splitscore" -> new SplitScoreGameType();
            default -> new X01GameType();
        };
    }
}
