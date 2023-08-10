package net.quombat.dododart.adapter.in.web;

import net.quombat.dododart.domain.GameType;
import net.quombat.dododart.domain.gametypes.CricketGameType;
import net.quombat.dododart.domain.gametypes.EliminationGameType;
import net.quombat.dododart.domain.gametypes.MiniminationGameType;
import net.quombat.dododart.domain.gametypes.SplitScoreGameType;
import net.quombat.dododart.domain.gametypes.X01GameType;

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
