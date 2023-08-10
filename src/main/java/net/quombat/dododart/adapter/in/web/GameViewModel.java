package net.quombat.dododart.adapter.in.web;

import net.quombat.dododart.domain.CricketGame;
import net.quombat.dododart.domain.Game;
import net.quombat.dododart.domain.MiniminationGame;
import net.quombat.dododart.domain.Player;
import net.quombat.dododart.domain.ScoreSegment;
import net.quombat.dododart.domain.SplitScoreGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

record GameViewModel(String gameMode, String round, List<PlayerViewModel> players,
                     String playerName, String currentScore, String currentPointsPerDart,
                     String currentPointsPerRound, String firstDart, String secondDart,
                     String thirdDart, String dartsSum, List<String> renderMessages) {
    static GameViewModel create(Game game) {
        if (game == null) {
            return null;
        }

        Player currentPlayer = game.determineCurrentPlayer();

        String gameMode = game.name();
        String round = "Runde: %s".formatted(round(game.getRound(), game.getMaxRounds()));
        List<PlayerViewModel> players = players(game);
        String playerName = "SPIELER %d".formatted(currentPlayer.getId());
        String currentPlayerScore = getCurrentPlayerScore(game);
        String currentPointsPerDart = "PPD %.1f".formatted(currentPlayer.getStatistics().getPointsPerDart());
        String currentPointsPerRound = "AVG %.1f".formatted(currentPlayer.getStatistics().getPointsPerRound());
        String firstDart = firstDart(game).map(GameViewModel::renderSegment).orElse("");
        String secondDart = secondDart(game).map(GameViewModel::renderSegment).orElse("");
        String thirdDart = thirdDart(game).map(GameViewModel::renderSegment).orElse("");
        String dartsSum = dartsSum(game);
        List<String> messages = renderMessages(game);

        return new GameViewModel(gameMode, round, players, playerName, currentPlayerScore, currentPointsPerDart, currentPointsPerRound, firstDart, secondDart, thirdDart, dartsSum, messages);
    }

    public static String dartsSum(Game game) {
        if (game instanceof SplitScoreGame) {
            return "";
        } else if (game instanceof CricketGame) {
            return "";
        } else if (game instanceof MiniminationGame) {
            int sum = game.getHits().stream().map(ScoreSegment::getPoints).reduce(0, Integer::sum);
            if (sum > 0) {
                return "%d".formatted(sum);
            } else {
                return "";
            }
        } else {
            int sum = game.getHits().stream().map(ScoreSegment::getScore).reduce(0, Integer::sum);
            if (sum > 0) {
                return "%d".formatted(sum);
            } else {
                return "";
            }
        }
    }

    private static Optional<ScoreSegment> firstDart(Game game) {
        return dartThrow(1, game);
    }

    private static Optional<ScoreSegment> secondDart(Game game) {
        return dartThrow(2, game);
    }

    private static Optional<ScoreSegment> thirdDart(Game game) {
        return dartThrow(3, game);
    }

    private static Optional<ScoreSegment> dartThrow(int throwNo, Game game) {
        return game.getHits().stream().skip(throwNo - 1).limit(1).findFirst();
    }

    private static String getCurrentPlayerScore(Game game) {
        if (game instanceof SplitScoreGame) {
            return splitScore(game);
        } else if (game instanceof CricketGame) {
            StringBuilder result = cricket(game);
            return result.toString();
        } else {
            return "" + game.determineCurrentPlayer().getScore();
        }
    }

    private static StringBuilder cricket(Game game) {
        List<Integer> integers = List.of(20, 19, 18, 17, 16, 15, 25);
        String header = integers.stream()
                .map(Object::toString)
                .collect(Collectors.joining(" | ", "", "<br>"));

        StringBuilder result = new StringBuilder(header);
        for (Player player : game.getPlayers()) {
            String collect = integers.stream()
                    .map(p -> "%02d".formatted(Math.min(3, player.getStatistics().getHitDistributionPerSlice().getOrDefault(p, 0))))
                    .collect(Collectors.joining(" | ", "", "<br>"));
            result.append(collect);
        }
        return result;
    }

    private static String splitScore(Game game) {
        return switch (game.getRound()) {
            case 1 -> "Nächstes Ziel: 15";
            case 2 -> "Nächstes Ziel: 16";
            case 3 -> "Nächstes Ziel: Double";
            case 4 -> "Nächstes Ziel: 17";
            case 5 -> "Nächstes Ziel: 18";
            case 6 -> "Nächstes Ziel: Triple";
            case 7 -> "Nächstes Ziel: 19";
            case 8 -> "Nächstes Ziel: 20";
            case 9 -> "Nächstes Ziel: Bull";
            default -> throw new IllegalStateException("Unexpected value: " + game.getRound());
        };
    }

    private static String round(int round, int maxRounds) {
        if (maxRounds > 0) {
            return "%d / %d".formatted(round, maxRounds);
        } else {
            return String.valueOf(round);
        }
    }

    private static String renderSegment(ScoreSegment segment) {
        if (segment.getMultiplier() == 1) {
            return "\uD83C\uDFAF %d".formatted(segment.getScore());
        } else {
            return "\uD83C\uDFAF %d (%s)".formatted(segment.getScore(), segment.toString());
        }
    }

    static List<PlayerViewModel> players(Game game) {
        return game.getPlayers().stream().map(p -> PlayerViewModel.create(p, game.getCurrentPlayerId()))
                .toList();
    }

    private static List<String> renderMessages(Game game) {
        List<String> messages = new ArrayList<>();

        if (game.isGameOver()) {
            Player winner = game.determineWinner();
            messages.add("GEWINNER");
            messages.add("SPIELER %d".formatted(winner.getId()));
        } else if (game.isBust()) {
            messages.add("Überworfen!");
            messages.add("Nächster Spieler");
            messages.add("Drücken Sie die Taste um fortzufahren");
        } else if (game.isTurnOver()) {
            messages.add("Nächster Spieler");
            messages.add("Drücken Sie die Taste um fortzufahren");
        }
        return messages;
    }

    public record PlayerViewModel(int id, String name, int score, boolean active) {
        public static PlayerViewModel create(Player p, int currentPlayerId) {
            return new PlayerViewModel(p.getId(), "Spieler %d".formatted(p.getId()), p.getScore(), p.getId() == currentPlayerId);
        }
    }
}