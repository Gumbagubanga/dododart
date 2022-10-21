package net.quombat.dododart.game.domain;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.AccessLevel;
import lombok.Getter;

public class Game {
    @Getter
    private final Rules rules;
    @Getter
    private final List<Player> players;
    @Getter
    private final int maxRounds;
    @Getter(value = AccessLevel.NONE)
    private int currentPlayerId;
    @Getter
    private int round;
    @Getter
    private GameState state;

    public Game(Rules rules, int noOfPlayers, int maxRounds) {
        this.rules = rules;
        this.players = IntStream.rangeClosed(1, noOfPlayers).boxed()
                .map(rules::createPlayer)
                .collect(Collectors.toList());
        this.maxRounds = maxRounds;
        this.currentPlayerId = 1;
        this.round = 1;
        this.state = GameState.In_Game;
    }

    public void hit(DartSegment segment) {
        if (state != GameState.In_Game) {
            return;
        }

        Player currentPlayer = determineCurrentPlayer();
        rules.hit(round, segment, currentPlayer, players);

        if (currentPlayer.isBust() || currentPlayer.isTurnOver()) {
            state = GameState.Switch_Player;
        }

        boolean gameOver = determineCurrentPlayerGameOver();
        if (gameOver) {
            state = GameState.Game_Over;
        }
    }

    public void nextPlayer() {
        if (state == GameState.Game_Over) {
            return;
        }

        Player currentPlayer = determineCurrentPlayer();
        currentPlayer.updateScore();

        int nextPlayerId = determineNextPlayer();

        boolean lastRoundPlayed = nextPlayerId == 1 && maxRounds > 0 && round + 1 > maxRounds;
        if (lastRoundPlayed) {
            state = GameState.Game_Over;
        } else {
            if (nextPlayerId == 1) {
                round++;
            }
            currentPlayerId = nextPlayerId;
            state = GameState.In_Game;
        }
    }

    public Player determineCurrentPlayer() {
        return players.stream().filter(p -> p.getId() == currentPlayerId).findFirst().orElseThrow();
    }

    public boolean isGameOver() {
        return state == GameState.Game_Over;
    }

    public Player determineWinner() {
        Player currentPlayer = determineCurrentPlayer();
        if (currentPlayer.isWinner()) {
            return currentPlayer;
        } else {
            return rules.leader(players);
        }
    }

    private boolean determineCurrentPlayerGameOver() {
        Player currentPlayer = determineCurrentPlayer();
        Player lastPlayer = players.get(players.size() - 1);
        boolean last = currentPlayer.equals(lastPlayer);

        return currentPlayer.isWinner() || (round == maxRounds && last && currentPlayer.isTurnOver());
    }

    private int determineNextPlayer() {
        int i = currentPlayerId + 1;
        if (i % (players.size() + 1) == 0) {
            i = 1;
        }
        return i;
    }
}
