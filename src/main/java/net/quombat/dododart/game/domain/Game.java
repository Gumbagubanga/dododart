package net.quombat.dododart.game.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class Game {
    private final GameType rules;
    private final List<Player> players;

    private List<ScoreSegment> hits;
    private int maxRounds;
    private int currentPlayerId;
    private int round;

    @Getter(AccessLevel.NONE)
    private GameState state;
    private int currentPlayerOldScore;
    private int currentScore;

    public Game(GameType rules, List<Player> players) {
        Objects.requireNonNull(rules);
        Objects.requireNonNull(players);

        this.rules = rules;
        this.players = players;
        init(rules);
    }

    private void init(GameType rules) {
        this.maxRounds = rules.maxRounds();
        this.currentPlayerId = 1;
        this.round = 1;
        this.state = GameState.In_Game;
        this.hits = new ArrayList<>();
        this.currentPlayerOldScore = rules.startScore();
        this.currentScore = currentPlayerOldScore;
    }

    public void hit(ScoreSegment segment) {
        if (state != GameState.In_Game) {
            return;
        }

        Player currentPlayer = determineCurrentPlayer();
        currentPlayer.hit(round, segment);
        hits.add(segment);

        currentScore = rules.calculateScore(this);

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
            IntStream.range(0, rules.throwsPerTurn() - hits.size()).forEach(i -> hits.add(ScoreSegment.MISS));
            currentPlayer.updateScore(rules.calculateScore(this));
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
            return rules.leader(this);
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
        return hits.size() == rules.throwsPerTurn();
    }

    public boolean isBust() {
        return rules.isBust(this);
    }

    public boolean isWinner() {
        return rules.isWinner(this);
    }

    public ScoreSegment lastDart() {
        return hits.stream().reduce((a, b) -> b).orElseThrow();
    }

    private enum GameState {
        In_Game,
        Game_Over,
        Switch_Player,
        ;
    }
}
