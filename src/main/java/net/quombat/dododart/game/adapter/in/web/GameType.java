package net.quombat.dododart.game.adapter.in.web;

import net.quombat.dododart.game.domain.EliminationRules;
import net.quombat.dododart.game.domain.Rules;
import net.quombat.dododart.game.domain.X01Rules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameType {
    ELIMINATION(301, true, new EliminationRules(0, 301)),
    FIVE_OH_ONE(501, false, new X01Rules(501, 0)),
    ;

    private final int targetScore;
    private final boolean elimination;
    private final Rules rules;

}
