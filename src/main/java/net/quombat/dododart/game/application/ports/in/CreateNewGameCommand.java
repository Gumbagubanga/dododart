package net.quombat.dododart.game.application.ports.in;

import net.quombat.dododart.game.domain.Rules;

public record CreateNewGameCommand(int noOfPlayers, Rules rules) {
}
