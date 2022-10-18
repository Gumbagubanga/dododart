package net.quombat.dododart.x01.adapter.in.web;

import net.quombat.dododart.x01.domain.DartSegment;
import net.quombat.dododart.x01.domain.X01Game;
import net.quombat.dododart.x01.domain.X01Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record X01ViewModel(String gameMode, String round, List<Player> players, int currentPlayer,
                           int currentScore, String currentPointsPerDart,
                           String currentPointsPerRound, String firstDart, String secondDart,
                           String thirdDart, int dartsSum, List<String> messages) {

    static X01ViewModel create(X01Game game) {
        X01Player currentPlayer = game.getCurrentPlayer();

        String gameMode = game.getGameMode();
        String round = round(game.getRound(), game.getMaxRounds());
        List<Player> players = players(game.getPlayers(), currentPlayer);
        int currentPlayerId = currentPlayer.getId();
        int currentPlayerScore = currentPlayer.getScore();
        String currentPointsPerDart = "%.1f".formatted(currentPlayer.getPointsPerDart());
        String currentPointsPerRound = "%.1f".formatted(currentPlayer.getPointsPerRound());
        String firstDart = currentPlayer.firstDart().map(X01ViewModel::renderSegment).orElse("");
        String secondDart = currentPlayer.secondDart().map(X01ViewModel::renderSegment).orElse("");
        String thirdDart = currentPlayer.thirdDart().map(X01ViewModel::renderSegment).orElse("");
        int dartsSum = currentPlayer.dartsSum();
        List<String> messages = messages(currentPlayer, game.isGameOver(), game.getPlayers());

        return new X01ViewModel(gameMode, round, players, currentPlayerId,
                currentPlayerScore, currentPointsPerDart, currentPointsPerRound,
                firstDart, secondDart, thirdDart, dartsSum, messages);
    }


    private static String round(int round, int maxRounds) {
        if (maxRounds > 0) {
            return "%d / %d".formatted(round, maxRounds);
        } else {
            return String.valueOf(round);
        }
    }

    private static String renderSegment(DartSegment segment) {
        return "%d (%s)".formatted(segment.getScore(), segment.toString());
    }

    static List<Player> players(List<X01Player> players, X01Player currentPlayer) {
        int currentPlayerScore = currentPlayer.getScore();
        return players.stream()
                .map(p -> getPlayer(currentPlayerScore, p, currentPlayer.getId()))
                .toList();
    }

    private static Player getPlayer(int currentPlayerScore, X01Player player, int currentPlayerId) {
        int id = player.getId();
        int score = (id == currentPlayerId) ? currentPlayerScore : player.getScore();
        return new Player(id, score);
    }

    private static List<String> messages(X01Player currentPlayer, boolean gameOver, List<X01Player> players) {
        List<String> messages = new ArrayList<>();

        if (currentPlayer.isWinner()) {
            messages.add("GEWINNER");
            messages.add("SPIELER %d".formatted(currentPlayer.getId()));
        } else if (currentPlayer.isBust()) {
            messages.add("Überworfen!");
            messages.add("Nächster Spieler");
            messages.add("Drücken Sie die Taste um fortzufahren");
        } else if (currentPlayer.isTurnOver()) {
            messages.add("Nächster Spieler");
            messages.add("Drücken Sie die Taste um fortzufahren");
        } else if (gameOver) {
            int winnerId = players.stream()
                    .max(Comparator.comparing(X01Player::getScore))
                    .map(X01Player::getId)
                    .orElseThrow();
            messages.add("GEWINNER");
            messages.add("SPIELER %d".formatted(winnerId));
        }
        return messages;
    }

    public record Player(int id, int score) {
    }
}
