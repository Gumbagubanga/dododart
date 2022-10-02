package net.quombat.dododart.five_oh_one.domain;

import net.quombat.dododart.shared.domain.DartSegment;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

@Testable
class FiveOhOnePlayerTest {

    @Test
    void new_player_starts_with_501_score() {
        FiveOhOnePlayer player = new FiveOhOnePlayer();

        Assertions.assertThat(player.getScore()).isEqualTo(501);
    }

    @Test
    void new_player_preliminary_score_after_single_20_is_481() {
        FiveOhOnePlayer player = new FiveOhOnePlayer();
        boolean bust = player.preliminaryScore(DartSegment.SINGLE_20);

        Assertions.assertThat(bust).isFalse();
        Assertions.assertThat(player.getPreliminaryScore()).isEqualTo(481);
    }

    @Test
    void player_with_20_score_bust_with_triple_7() {
        FiveOhOnePlayer player = new FiveOhOnePlayer(20);
        boolean bust = player.preliminaryScore(DartSegment.TRIPLE_07);

        Assertions.assertThat(bust).isTrue();
        Assertions.assertThat(player.getPreliminaryScore()).isEqualTo(-1);
    }

    @Test
    void player_accepts_positive_score() {
        FiveOhOnePlayer player = new FiveOhOnePlayer(20);
        player.preliminaryScore(DartSegment.TRIPLE_06);
        boolean bust = player.acceptScore();

        Assertions.assertThat(bust).isFalse();
        Assertions.assertThat(player.getScore()).isEqualTo(2);
    }

    @Test
    void player_denies_negative_score() {
        FiveOhOnePlayer player = new FiveOhOnePlayer(20);
        player.preliminaryScore(DartSegment.TRIPLE_07);
        boolean bust = player.acceptScore();

        Assertions.assertThat(bust).isTrue();
        Assertions.assertThat(player.getScore()).isEqualTo(20);
    }

    @Test
    void player_with_0_score_wins() {
        FiveOhOnePlayer player = new FiveOhOnePlayer(20);
        boolean bust = player.preliminaryScore(DartSegment.SINGLE_20);

        Assertions.assertThat(bust).isFalse();
        Assertions.assertThat(player.getScore()).isEqualTo(0);
        Assertions.assertThat(player.winner()).isTrue();
    }
}