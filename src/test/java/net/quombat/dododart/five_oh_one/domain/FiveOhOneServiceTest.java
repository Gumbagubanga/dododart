package net.quombat.dododart.five_oh_one.domain;

import net.quombat.dododart.five_oh_one.FiveOhOneService;
import net.quombat.dododart.shared.domain.Dart;
import net.quombat.dododart.shared.domain.DartSegment;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

@Testable
class FiveOhOneServiceTest {

    @Test
    void new_player_preliminary_score_after_single_20_is_481() {
        FiveOhOneService game = new FiveOhOneService();
        game.init(1);
        game.hit(new Dart(DartSegment.SINGLE_20));

        Assertions.assertThat(game.isCurrentPlayerBust()).isFalse();
        Assertions.assertThat(game.getCurrentPlayerPreliminaryScore()).isEqualTo(481);
    }

    @Test
    void player_with_20_score_bust_with_triple_7() {
        FiveOhOneService game = new FiveOhOneService();
        game.init(1);
        game.getCurrentPlayer().updateScore(20);

        game.hit(new Dart(DartSegment.TRIPLE_07));

        Assertions.assertThat(game.isCurrentPlayerBust()).isTrue();
        Assertions.assertThat(game.getCurrentPlayerPreliminaryScore()).isEqualTo(481);
    }

    @Test
    void player_accepts_positive_score() {
        FiveOhOneService game = new FiveOhOneService();
        game.init(1);
        game.getCurrentPlayer().updateScore(20);

        game.hit(new Dart(DartSegment.SINGLE_06));
        game.hit(new Dart(DartSegment.SINGLE_06));
        game.hit(new Dart(DartSegment.SINGLE_06));

        Assertions.assertThat(game.isCurrentPlayerBust()).isFalse();
        Assertions.assertThat(game.getCurrentPlayer().getScore()).isEqualTo(2);
        Assertions.assertThat(game.isGameover()).isFalse();
    }

    @Test
    void player_with_0_score_wins() {
        FiveOhOneService game = new FiveOhOneService();
        game.init(1);
        game.getCurrentPlayer().updateScore(20);

        game.hit(new Dart(DartSegment.SINGLE_20));

        Assertions.assertThat(game.isCurrentPlayerBust()).isFalse();
        Assertions.assertThat(game.getCurrentPlayer().getScore()).isEqualTo(0);
        Assertions.assertThat(game.isGameover()).isTrue();
    }
}