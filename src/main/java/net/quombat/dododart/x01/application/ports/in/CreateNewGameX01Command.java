package net.quombat.dododart.x01.application.ports.in;

public record CreateNewGameX01Command(int noOfPlayers, int targetScore, boolean elimination,
                                      Integer maxRounds) {
}
