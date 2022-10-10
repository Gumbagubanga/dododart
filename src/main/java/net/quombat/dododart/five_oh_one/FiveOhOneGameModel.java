package net.quombat.dododart.five_oh_one;

import net.quombat.dododart.shared.domain.Dart;
import net.quombat.dododart.shared.domain.DartSegment;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;

@Builder
public record FiveOhOneGameModel(String gameMode, int round, List<Player> players,
                                 int currentPlayer,
                                 int currentScore, String currentPointsPerDart,
                                 String currentPointsPerRound,
                                 String firstDart, String secondDart, String thirdDart,
                                 int dartsSum, List<String> messages) {

    static FiveOhOneGameModel create(FiveOhOneService service) {
        return builder()
                .gameMode("501")
                .round(service.getRound())
                .players(players(service))
                .currentPlayer(service.getCurrentPlayer().getId())
                .currentScore(getCurrentPlayerScore(service))
                .currentPointsPerDart("%.1f".formatted(service.getCurrentPlayer().getPointsPerDart()))
                .currentPointsPerRound("%.1f".formatted(service.getCurrentPlayer().getPointsPerRound()))
                .firstDart(dart(service, 0))
                .secondDart(dart(service, 1))
                .thirdDart(dart(service, 2))
                .dartsSum(dartsSum(service))
                .messages(messages(service))
                .build();
    }

    private static String dart(FiveOhOneService service, int n) {
        return service.getDarts().stream()
                .skip(n)
                .limit(1)
                .findFirst()
                .map(Dart::segment)
                .map(s -> "%d (%s)".formatted(s.getScore(), s.toString()))
                .orElse("");
    }

    static int getCurrentPlayerScore(FiveOhOneService service) {
        int preliminaryScore = service.getCurrentPlayerPreliminaryScore();
        return (preliminaryScore < 0 ? service.getCurrentPlayer().getScore() : preliminaryScore);
    }

    static List<Player> players(FiveOhOneService service) {
        int currentPlayerScore = getCurrentPlayerScore(service);
        return service.getPlayers().stream()
                .map(p -> new Player(p.getId(), p.getId() == service.getCurrentPlayer().getId() ? currentPlayerScore : p.getScore()))
                .toList();
    }

    static int dartsSum(FiveOhOneService service) {
        return service.getDarts().stream().map(Dart::segment).map(DartSegment::getScore).reduce(0, Integer::sum);
    }

    static List<String> messages(FiveOhOneService service) {
        List<String> messages = new ArrayList<>();
        if (service.getCurrentPlayerPreliminaryScore() == 0) {
            messages.add("GEWINNER");
            messages.add("SPIELER %d".formatted(service.getCurrentPlayer().getId()));
        } else if (service.getCurrentPlayerPreliminaryScore() < 0) {
            messages.add("Überworfen!");
            messages.add("Nächster Spieler");
            messages.add("Drücken Sie die Taste um fortzufahren");
        } else if (service.getDarts().size() == 3) {
            messages.add("Nächster Spieler");
            messages.add("Drücken Sie die Taste um fortzufahren");
        }
        return messages;
    }


    public record Player(int id, int score) {
    }
}
