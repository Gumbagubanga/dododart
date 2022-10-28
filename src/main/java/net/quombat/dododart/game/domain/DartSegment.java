package net.quombat.dododart.game.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DartSegment {
    MISS(0, 0),

    SINGLE_01(1, 1),
    SINGLE_02(2, 1),
    SINGLE_03(3, 1),
    SINGLE_04(4, 1),
    SINGLE_05(5, 1),
    SINGLE_06(6, 1),
    SINGLE_07(7, 1),
    SINGLE_08(8, 1),
    SINGLE_09(9, 1),
    SINGLE_10(10, 1),
    SINGLE_11(11, 1),
    SINGLE_12(12, 1),
    SINGLE_13(13, 1),
    SINGLE_14(14, 1),
    SINGLE_15(15, 1),
    SINGLE_16(16, 1),
    SINGLE_17(17, 1),
    SINGLE_18(18, 1),
    SINGLE_19(19, 1),
    SINGLE_20(20, 1),
    SINGLE_BULL(25, 1),

    DOUBLE_01(1, 2),
    DOUBLE_02(2, 2),
    DOUBLE_03(3, 2),
    DOUBLE_04(4, 2),
    DOUBLE_05(5, 2),
    DOUBLE_06(6, 2),
    DOUBLE_07(7, 2),
    DOUBLE_08(8, 2),
    DOUBLE_09(9, 2),
    DOUBLE_10(10, 2),
    DOUBLE_11(11, 2),
    DOUBLE_12(12, 2),
    DOUBLE_13(13, 2),
    DOUBLE_14(14, 2),
    DOUBLE_15(15, 2),
    DOUBLE_16(16, 2),
    DOUBLE_17(17, 2),
    DOUBLE_18(18, 2),
    DOUBLE_19(19, 2),
    DOUBLE_20(20, 2),
    DOUBLE_BULL(25, 2),

    TRIPLE_01(1, 3),
    TRIPLE_02(2, 3),
    TRIPLE_03(3, 3),
    TRIPLE_04(4, 3),
    TRIPLE_05(5, 3),
    TRIPLE_06(6, 3),
    TRIPLE_07(7, 3),
    TRIPLE_08(8, 3),
    TRIPLE_09(9, 3),
    TRIPLE_10(10, 3),
    TRIPLE_11(11, 3),
    TRIPLE_12(12, 3),
    TRIPLE_13(13, 3),
    TRIPLE_14(14, 3),
    TRIPLE_15(15, 3),
    TRIPLE_16(16, 3),
    TRIPLE_17(17, 3),
    TRIPLE_18(18, 3),
    TRIPLE_19(19, 3),
    TRIPLE_20(20, 3),
    ;

    private final int score;
    private final int multiplier;

    public static final Set<DartSegment> doubles = Set.of(
            DOUBLE_01, DOUBLE_02, DOUBLE_03, DOUBLE_04, DOUBLE_05,
            DOUBLE_06, DOUBLE_07, DOUBLE_08, DOUBLE_09, DOUBLE_10,
            DOUBLE_11, DOUBLE_12, DOUBLE_13, DOUBLE_14, DOUBLE_15,
            DOUBLE_16, DOUBLE_17, DOUBLE_18, DOUBLE_19, DOUBLE_20);

    public static final Set<DartSegment> triples = Set.of(
            TRIPLE_01, TRIPLE_02, TRIPLE_03, TRIPLE_04, TRIPLE_05,
            TRIPLE_06, TRIPLE_07, TRIPLE_08, TRIPLE_09, TRIPLE_10,
            TRIPLE_11, TRIPLE_12, TRIPLE_13, TRIPLE_14, TRIPLE_15,
            TRIPLE_16, TRIPLE_17, TRIPLE_18, TRIPLE_19, TRIPLE_20);

    public static final Set<DartSegment> bulls = Set.of(SINGLE_BULL, DOUBLE_BULL);

    public static final Set<DartSegment> twenties = Set.of(SINGLE_20, DOUBLE_20, TRIPLE_20);

    public static final Set<DartSegment> nineteens = Set.of(SINGLE_19, DOUBLE_19, TRIPLE_19);

    public static final Set<DartSegment> eighteens = Set.of(SINGLE_18, DOUBLE_18, TRIPLE_18);

    public static final Set<DartSegment> seventeens = Set.of(SINGLE_17, DOUBLE_17, TRIPLE_17);

    public static final Set<DartSegment> sixteens = Set.of(SINGLE_16, DOUBLE_16, TRIPLE_16);

    public static final Set<DartSegment> fifteens = Set.of(SINGLE_15, DOUBLE_15, TRIPLE_15);

    public static final Set<DartSegment> highs = Stream.of(bulls,
            twenties, nineteens, eighteens,
            seventeens, sixteens, fifteens).flatMap(Collection::stream).collect(Collectors.toSet());

    public static DartSegment from(int value) {
        int multiplier = value / 100;
        int score = value % 100;

        return Arrays.stream(values())
                .filter(s -> s.score == score && s.multiplier == multiplier)
                .findFirst()
                .orElseThrow();
    }

    public int getScore() {
        return score * multiplier;
    }

    @Override
    public String toString() {
        if (this == MISS) {
            return "Miss";
        } else if (this == SINGLE_BULL) {
            return "Bull";
        } else if (this == DOUBLE_BULL) {
            return "DBull";
        } else if (multiplier == 2) {
            return "D" + score;
        } else if (multiplier == 3) {
            return "T" + score;
        }
        return "S" + score;
    }
}
