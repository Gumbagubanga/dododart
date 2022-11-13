package net.quombat.dododart.game.adapter.in.web;

import net.quombat.dododart.game.application.ports.in.RenderUseCase;
import net.quombat.dododart.game.domain.DartSegment;
import net.quombat.dododart.game.domain.Game;
import net.quombat.dododart.game.domain.Player;
import net.quombat.dododart.game.domain.SplitScoreRules;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/x01")
@Controller
class RenderController {

    private final RenderUseCase renderUseCase;

    @GetMapping("/score")
    public ModelAndView score() {
        Game game = renderUseCase.fetchGame();
        GameViewModel viewModel = GameViewModel.create(game);
        return new ModelAndView("fragments/x01score", "model", viewModel);
    }

    record GameViewModel(String gameMode, String round, List<PlayerViewModel> players,
                         int currentPlayer,
                         String currentScore, String currentPointsPerDart,
                         String currentPointsPerRound, String firstDart, String secondDart,
                         String thirdDart, int dartsSum, List<String> renderMessages) {

        static GameViewModel create(Game game) {
            Player currentPlayer = game.determineCurrentPlayer();

            String gameMode = game.getRules().gameType();
            String round = round(game.getRound(), game.getMaxRounds());
            List<PlayerViewModel> players = players(game);
            int currentPlayerId = currentPlayer.getId();
            String currentPlayerScore = getCurrentPlayerScore(game);
            String currentPointsPerDart = "%.1f".formatted(currentPlayer.getStatistics().getPointsPerDart());
            String currentPointsPerRound = "%.1f".formatted(currentPlayer.getStatistics().getPointsPerRound());
            String firstDart = game.firstDart().map(GameViewModel::renderSegment).orElse("");
            String secondDart = game.secondDart().map(GameViewModel::renderSegment).orElse("");
            String thirdDart = game.thirdDart().map(GameViewModel::renderSegment).orElse("");
            int dartsSum = game.dartsSum();
            List<String> messages = renderMessages(game);

            return new GameViewModel(gameMode, round, players, currentPlayerId,
                    currentPlayerScore, currentPointsPerDart, currentPointsPerRound,
                    firstDart, secondDart, thirdDart, dartsSum, messages);
        }

        private static String getCurrentPlayerScore(Game game) {
            if (game.getRules() instanceof SplitScoreRules) {
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
                    default ->
                            throw new IllegalStateException("Unexpected value: " + game.getRound());
                };
            } else {
                return "" + game.determineCurrentPlayer().getScore();
            }
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

        static List<PlayerViewModel> players(Game game) {
            return game.getPlayers().stream()
                    .map(p -> new PlayerViewModel(p.getId(), p.getScore()))
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

        public record PlayerViewModel(int id, int score) {
        }
    }
}
