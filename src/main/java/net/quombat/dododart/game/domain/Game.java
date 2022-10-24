package net.quombat.dododart.game.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class Game {
    @Getter(value = AccessLevel.NONE)
    private final List<DartSegment> hits;
    private final Rules rules;
    private final List<Player> players;
    private final int maxRounds;
    private int currentPlayerId;
    private int round;
    private GameState state;
    private int currentScore;

    public Game(Rules rules, int noOfPlayers, int maxRounds) {
        this.rules = rules;
        this.players = IntStream.rangeClosed(1, noOfPlayers).boxed()
                .map(playerNo -> new Player(playerNo, rules.startScore()))
                .collect(Collectors.toList());
        this.maxRounds = maxRounds;
        this.currentPlayerId = 1;
        this.round = 1;
        this.state = GameState.In_Game;
        this.hits = new ArrayList<>();
        this.currentScore = rules.startScore();
    }

    public void hit(DartSegment segment) {
        if (state != GameState.In_Game) {
            return;
        }

        Player currentPlayer = determineCurrentPlayer();
        currentPlayer.hit(round, segment);
        hits.add(segment);

        int preliminaryScore = rules.calculateScore(this);

        if (isBust()) {
            currentPlayer.updateScore(currentScore);
        } else {
            currentPlayer.updateScore(preliminaryScore);
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
            IntStream.range(0, 3 - hits.size()).forEach(i -> hits.add(DartSegment.MISS));
            currentPlayer.updateScore(rules.calculateScore(this));
        }

        int nextPlayerId = determineNextPlayer();

        boolean lastRoundPlayed = nextPlayerId == 1 && maxRounds > 0 && round + 1 > maxRounds;
        if (lastRoundPlayed) {
            state = GameState.Game_Over;
        } else {
            if (nextPlayerId == 1) {
                round++;
            }
            currentPlayerId = nextPlayerId;
            currentScore = determineCurrentPlayer().getScore();
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

    private int determineNextPlayer() {
        int i = currentPlayerId + 1;
        if (i % (players.size() + 1) == 0) {
            i = 1;
        }
        return i;
    }

    public boolean isTurnOver() {
        return hits.size() == 3;
    }

    public Optional<DartSegment> firstDart() {
        return dartThrow(1);
    }

    public Optional<DartSegment> secondDart() {
        return dartThrow(2);
    }

    public Optional<DartSegment> thirdDart() {
        return dartThrow(3);
    }

    public int dartsSum() {
        return hits.stream().map(DartSegment::getScore).reduce(0, Integer::sum);
    }

    public Optional<DartSegment> dartThrow(int throwNo) {
        return hits.stream().skip(throwNo - 1).limit(1).findFirst();
    }

    public boolean isBust() {
        return rules.isBust(this);
    }

    public boolean isWinner() {
        return rules.isWinner(this);
    }

}
