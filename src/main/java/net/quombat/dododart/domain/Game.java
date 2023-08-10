package net.quombat.dododart.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import lombok.AccessLevel;
import lombok.Getter;

@Getter
public abstract class Game {
    private List<Player> players;

    private final List<ScoreSegment> hits;
    private final int maxRounds;
    private int currentPlayerId;
    private int round;

    @Getter(AccessLevel.NONE)
    private GameState state;
    private int currentPlayerOldScore;
    private int currentScore;

    public Game() {
        this.maxRounds = maxRounds();
        this.currentPlayerId = 1;
        this.round = 1;
        this.state = GameState.In_Game;
        this.hits = new ArrayList<>();
        this.currentPlayerOldScore = startScore();
        this.currentScore = currentPlayerOldScore;
    }

    public abstract String name();

    public abstract boolean isBust();

    public abstract boolean isWinner();

    public abstract int calculateScore();

    public abstract int startScore();

    public abstract Player leader();

    public abstract int maxRounds();

    public void start(List<Player> players) {
        Objects.requireNonNull(players);

        this.players = players;
    }

    public void hit(ScoreSegment segment) {
        if (state != GameState.In_Game) {
            return;
        }

        Player currentPlayer = determineCurrentPlayer();
        currentPlayer.hit(round, segment);
        hits.add(segment);

        currentScore = this.calculateScore();

        if (isBust()) {
            currentPlayer.updateScore(currentPlayerOldScore);
        } else {
            currentPlayer.updateScore(currentScore);
        }

        if (isBust() || isTurnOver()) {
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
        if (state == GameState.In_Game) {
            IntStream.range(0, this.throwsPerTurn() - hits.size()).forEach(i -> hits.add(ScoreSegment.MISS));
            currentPlayer.updateScore(this.calculateScore());
        }

        int nextPlayerId = determineNextPlayerId();

        boolean lastRoundPlayed = nextPlayerId == 1 && maxRounds > 0 && round + 1 > maxRounds;
        if (lastRoundPlayed) {
            state = GameState.Game_Over;
        } else {
            if (nextPlayerId == 1) {
                round++;
            }
            currentPlayerId = nextPlayerId;
            currentPlayerOldScore = determineCurrentPlayer().getScore();
            currentScore = currentPlayerOldScore;
            state = GameState.In_Game;
            hits.clear();
        }
    }

    public Player determineCurrentPlayer() {
        return players.stream().filter(p -> p.getId() == currentPlayerId).findFirst().orElseThrow();
    }

    public boolean isGameOver() {
        return state == GameState.Game_Over;
    }

    public boolean isSwitchPlayerState() {
        return state == GameState.Switch_Player;
    }

    public Player determineWinner() {
        Player currentPlayer = determineCurrentPlayer();
        if (isWinner()) {
            return currentPlayer;
        } else {
            return this.leader();
        }
    }

    private boolean determineCurrentPlayerGameOver() {
        Player currentPlayer = determineCurrentPlayer();
        Player lastPlayer = players.get(players.size() - 1);
        boolean last = currentPlayer.equals(lastPlayer);

        return isWinner() || (round == maxRounds && last && isTurnOver());
    }

    private int determineNextPlayerId() {
        int nextPlayerId = currentPlayerId + 1;
        if (nextPlayerId % (players.size() + 1) == 0) {
            nextPlayerId = 1;
        }
        return nextPlayerId;
    }

    public boolean isTurnOver() {
        return hits.size() == this.throwsPerTurn();
    }

    public ScoreSegment lastDart() {
        return hits.stream().reduce((a, b) -> b).orElseThrow();
    }

    public int throwsPerTurn() {
        return 3;
    }

    private enum GameState {
        In_Game,
        Game_Over,
        Switch_Player,
    }
}
