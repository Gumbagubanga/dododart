package net.quombat.dododart.elimination;

import net.quombat.dododart.shared.domain.Dart;
import net.quombat.dododart.shared.domain.DartSegment;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Getter;

@Getter
@Service
public class EliminationService {

    private List<EliminationPlayer> players;
    private List<Dart> darts;
    private int currentPlayer;
    private boolean gameover;
    private boolean switchPlayerState;
    private int round = 1;
    private int targetScore;
    private int maxRounds;
    boolean enabled = false;

    public void init(int noOfPlayers) {
        this.players = IntStream.rangeClosed(1, noOfPlayers).boxed()
                .map(playerNo -> new EliminationPlayer(playerNo, 0))
                .collect(Collectors.toList());
        this.currentPlayer = 0;
        this.darts = new ArrayList<>();
        this.gameover = false;
        this.switchPlayerState = false;
        this.enabled = true;
        this.round = 1;
        this.targetScore = 301;
        this.maxRounds = 10;
    }

    public void hit(Dart dart) {
        if (gameover || switchPlayerState) {
            return;
        }

        darts.add(dart);

        int preliminaryScore = getCurrentPlayerPreliminaryScore();

        if (preliminaryScore <= targetScore) {
            getCurrentPlayer().addDart(round, dart.segment());
        }

        players.stream().filter(p -> p.getScore() == preliminaryScore).forEach(p -> p.updateScore(0));

        if (preliminaryScore == targetScore) {
            gameover = true;
            enabled = false;
        } else {
            if (preliminaryScore > targetScore) {
                switchPlayerState = true;
            } else {
                if (darts.size() == 3) {
                    switchPlayerState = true;
                }
            }
        }
    }

    public void nextPlayer() {
        int currentPlayerPreliminaryScore = getCurrentPlayerPreliminaryScore();
        if (currentPlayerPreliminaryScore <= targetScore) {
            getCurrentPlayer().updateScore(currentPlayerPreliminaryScore);
        }

        currentPlayer = determineNextPlayer();
        darts.clear();
        switchPlayerState = false;
        if (currentPlayer == 0) {
            round++;
        }
        if (round == maxRounds) {
            gameover = true;
        }
    }

    private int preliminaryDartScore() {
        return darts.stream().map(Dart::segment).map(DartSegment::getScore).reduce(0, Integer::sum);
    }

    public EliminationPlayer getCurrentPlayer() {
        return players.get(currentPlayer);
    }

    private int determineNextPlayer() {
        int i = currentPlayer + 1;
        if (i % players.size() == 0) {
            i = 0;
        }
        return i;
    }

    public int getCurrentPlayerPreliminaryScore() {
        return getCurrentPlayer().getScore() + preliminaryDartScore();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getRound() {
        return round;
    }

    public void disable() {
        enabled = false;
    }
}
