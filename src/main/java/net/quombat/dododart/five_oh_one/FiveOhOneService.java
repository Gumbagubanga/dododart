package net.quombat.dododart.five_oh_one;

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
public class FiveOhOneService {

    private List<FiveOhOnePlayer> players;
    private List<Dart> darts;
    private int currentPlayer;
    private boolean gameover;
    private boolean switchPlayerState;
    private int round = 1;
    boolean enabled = false;

    public void init(int noOfPlayers) {
        this.players = IntStream.rangeClosed(1, noOfPlayers).boxed()
                .map(playerNo -> new FiveOhOnePlayer(playerNo, 501))
                .collect(Collectors.toList());
        this.currentPlayer = 0;
        this.darts = new ArrayList<>();
        this.gameover = false;
        this.switchPlayerState = false;
        this.enabled = true;
        this.round = 1;
    }

    public void hit(Dart dart) {
        if (gameover || switchPlayerState) {
            return;
        }

        darts.add(dart);

        int preliminaryScore = getCurrentPlayerPreliminaryScore();

        if (preliminaryScore >= 0) {
            getCurrentPlayer().addDart(round, dart.segment());
        }

        if (preliminaryScore == 0) {
            gameover = true;
            enabled = false;
        } else if (preliminaryScore > 0) {
            if (darts.size() == 3) {
                switchPlayerState = true;
            }
        } else {
            switchPlayerState = true;
        }
    }

    public void nextPlayer() {
        int currentPlayerPreliminaryScore = getCurrentPlayerPreliminaryScore();
        if (currentPlayerPreliminaryScore >= 0) {
            getCurrentPlayer().updateScore(currentPlayerPreliminaryScore);
        }

        currentPlayer = determineNextPlayer();
        darts.clear();
        switchPlayerState = false;
        if (currentPlayer == 0) {
            round++;
        }
    }

    private int preliminaryDartScore() {
        return darts.stream().map(Dart::segment).map(DartSegment::getScore).reduce(0, Integer::sum);
    }

    public FiveOhOnePlayer getCurrentPlayer() {
        return players.get(currentPlayer);
    }

    private int determineNextPlayer() {
        int i = currentPlayer + 1;
        if (i % players.size() == 0) {
            i = 0;
        }
        return i;
    }

    public boolean isCurrentPlayerBust() {
        return getCurrentPlayerPreliminaryScore() < 0;
    }

    public int getCurrentPlayerPreliminaryScore() {
        return getCurrentPlayer().getScore() - preliminaryDartScore();
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
