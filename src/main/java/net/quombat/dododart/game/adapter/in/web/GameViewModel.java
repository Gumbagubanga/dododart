package net.quombat.dododart.game.adapter.in.web;

import net.quombat.dododart.game.domain.DartSegment;
import net.quombat.dododart.game.domain.Game;
import net.quombat.dododart.game.domain.Player;

import java.util.ArrayList;
import java.util.List;

public record GameViewModel(String gameMode, String round, List<PlayerViewModel> players,
                            int currentPlayer,
                            int currentScore, String currentPointsPerDart,
                            String currentPointsPerRound, String firstDart, String secondDart,
                            String thirdDart, int dartsSum, List<String> renderMessages) {

    static GameViewModel create(Game game) {
        Player currentPlayer = game.determineCurrentPlayer();

        String gameMode = game.getRules().gameType();
        String round = round(game.getRound(), game.getMaxRounds());
        List<PlayerViewModel> players = players(game.getPlayers(), currentPlayer);
        int currentPlayerId = currentPlayer.getId();
        int currentPlayerScore = currentPlayer.currentScore();
        String currentPointsPerDart = "%.1f".formatted(currentPlayer.getPointsPerDart());
        String currentPointsPerRound = "%.1f".formatted(currentPlayer.getPointsPerRound());
        String firstDart = currentPlayer.firstDart().map(GameViewModel::renderSegment).orElse("");
        String secondDart = currentPlayer.secondDart().map(GameViewModel::renderSegment).orElse("");
        String thirdDart = currentPlayer.thirdDart().map(GameViewModel::renderSegment).orElse("");
        int dartsSum = currentPlayer.dartsSum();
        List<String> messages = renderMessages(game);

        return new GameViewModel(gameMode, round, players, currentPlayerId,
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

    static List<PlayerViewModel> players(List<Player> players, Player currentPlayer) {
        int currentPlayerScore = currentPlayer.currentScore();
        return players.stream()
                .map(p -> getPlayer(currentPlayerScore, p, currentPlayer.getId()))
                .toList();
    }

    private static PlayerViewModel getPlayer(int currentPlayerScore, Player player, int currentPlayerId) {
        int id = player.getId();
        int score = (id == currentPlayerId) ? currentPlayerScore : player.currentScore();
        return new PlayerViewModel(id, score);
    }

    private static List<String> renderMessages(Game game) {
        Player currentPlayer = game.determineCurrentPlayer();
        List<String> messages = new ArrayList<>();

        if (game.isGameOver()) {
            Player winner = game.determineWinner();
            messages.add("GEWINNER");
            messages.add("SPIELER %d".formatted(winner.getId()));
        } else if (currentPlayer.isBust()) {
            messages.add("Überworfen!");
            messages.add("Nächster Spieler");
            messages.add("Drücken Sie die Taste um fortzufahren");
        } else if (currentPlayer.isTurnOver()) {
            messages.add("Nächster Spieler");
            messages.add("Drücken Sie die Taste um fortzufahren");
        }
        return messages;
    }

    public record PlayerViewModel(int id, int score) {
    }
}
