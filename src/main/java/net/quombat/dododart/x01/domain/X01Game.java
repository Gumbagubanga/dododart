package net.quombat.dododart.x01.domain;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class X01Game {
    private final List<X01Player> players;
    private final String gameMode;
    private final boolean elimination;
    private final int maxRounds;

    @Getter(value = AccessLevel.NONE)
    private int currentPlayerId = 1;

    private int round = 1;
    private boolean gameOver = false;
    private boolean switchPlayerState = false;

    public X01Game(int noOfPlayers, int targetScore, boolean elimination, int maxRounds) {
        this.players = IntStream.rangeClosed(1, noOfPlayers).boxed().map(playerNo -> new X01Player(playerNo, targetScore, elimination)).collect(Collectors.toList());
        this.gameMode = gameMode(targetScore, elimination);
        this.elimination = elimination;
        this.maxRounds = maxRounds;
    }

    public void hit(DartSegment segment) {
        if (gameOver || switchPlayerState) {
            return;
        }

        X01Player currentPlayer = getCurrentPlayer();

        currentPlayer.hit(round, segment);

        if (elimination) {
            players.stream()
                    .filter(Predicate.not(currentPlayer::equals))
                    .filter(p -> p.getScore() != 0)
                    .filter(p -> p.getScore() == currentPlayer.getScore())
                    .forEach(X01Player::eliminate);
        }

        if (currentPlayer.isBust() || currentPlayer.isTurnOver()) {
            switchPlayerState = true;
        }

        updateGameOver();
        if (gameOver) {
            switchPlayerState = false;
        }
    }

    public void nextPlayer() {
        X01Player currentPlayer = getCurrentPlayer();
        currentPlayer.updateScore();

        updateGameOver();
        if (!gameOver) {
            currentPlayerId = determineNextPlayer();
            switchPlayerState = false;
            if (currentPlayerId == 1) {
                round++;
            }
        }
    }

    private void updateGameOver() {
        X01Player currentPlayer = getCurrentPlayer();
        boolean last = currentPlayer.equals(lastPlayer());

        gameOver = currentPlayer.isWinner() || (round == maxRounds && last);
    }

    public X01Player getCurrentPlayer() {
        return players.stream().filter(p -> p.getId() == currentPlayerId).findFirst().orElseThrow();
    }

    private X01Player lastPlayer() {
        return players.get(players.size() - 1);
    }

    private int determineNextPlayer() {
        int i = currentPlayerId + 1;
        if (i % (players.size() + 1) == 0) {
            i = 1;
        }
        return i;
    }

    private static String gameMode(int targetScore, boolean elimination) {
        if (elimination) {
            return "%d Elimination".formatted(targetScore);
        } else {
            return "%d".formatted(targetScore);
        }
    }
}
