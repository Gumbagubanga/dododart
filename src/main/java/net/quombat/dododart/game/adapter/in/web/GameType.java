package net.quombat.dododart.game.adapter.in.web;

import net.quombat.dododart.game.domain.EliminationRules;
import net.quombat.dododart.game.domain.Rules;
import net.quombat.dododart.game.domain.SplitScoreRules;
import net.quombat.dododart.game.domain.X01Rules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameType {
    ELIMINATION(new EliminationRules(0, 301, 10)),
    FIVE_OH_ONE(new X01Rules(501, 0, -1)),
    SPLIT_SCORE(new SplitScoreRules(40)),
    ;

    private final Rules rules;

}
