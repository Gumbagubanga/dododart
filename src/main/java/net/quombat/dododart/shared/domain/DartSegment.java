package net.quombat.dododart.shared.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum DartSegment {

    SINGLE_01(1, false, false),
    SINGLE_02(2, false, false),
    SINGLE_03(3, false, false),
    SINGLE_04(4, false, false),
    SINGLE_05(5, false, false),
    SINGLE_06(6, false, false),
    SINGLE_07(7, false, false),
    SINGLE_08(8, false, false),
    SINGLE_09(9, false, false),
    SINGLE_10(10, false, false),
    SINGLE_11(11, false, false),
    SINGLE_12(12, false, false),
    SINGLE_13(13, false, false),
    SINGLE_14(14, false, false),
    SINGLE_15(15, false, false),
    SINGLE_16(16, false, false),
    SINGLE_17(17, false, false),
    SINGLE_18(18, false, false),
    SINGLE_19(19, false, false),
    SINGLE_20(20, false, false),
    SINGLE_BULL(25, false, false),

    DOUBLE_01(2, true, false),
    DOUBLE_02(4, true, false),
    DOUBLE_03(6, true, false),
    DOUBLE_04(8, true, false),
    DOUBLE_05(10, true, false),
    DOUBLE_06(12, true, false),
    DOUBLE_07(14, true, false),
    DOUBLE_08(16, true, false),
    DOUBLE_09(18, true, false),
    DOUBLE_10(20, true, false),
    DOUBLE_11(22, true, false),
    DOUBLE_12(24, true, false),
    DOUBLE_13(26, true, false),
    DOUBLE_14(28, true, false),
    DOUBLE_15(30, true, false),
    DOUBLE_16(32, true, false),
    DOUBLE_17(34, true, false),
    DOUBLE_18(36, true, false),
    DOUBLE_19(38, true, false),
    DOUBLE_20(40, true, false),
    DOUBLE_BULL(50, true, false),

    TRIPLE_01(3, false, true),
    TRIPLE_02(6, false, true),
    TRIPLE_03(9, false, true),
    TRIPLE_04(12, false, true),
    TRIPLE_05(15, false, true),
    TRIPLE_06(18, false, true),
    TRIPLE_07(21, false, true),
    TRIPLE_08(24, false, true),
    TRIPLE_09(27, false, true),
    TRIPLE_10(30, false, true),
    TRIPLE_11(33, false, true),
    TRIPLE_12(36, false, true),
    TRIPLE_13(39, false, true),
    TRIPLE_14(42, false, true),
    TRIPLE_15(45, false, true),
    TRIPLE_16(48, false, true),
    TRIPLE_17(51, false, true),
    TRIPLE_18(54, false, true),
    TRIPLE_19(57, false, true),
    TRIPLE_20(60, false, true),
    ;

    private final int score;
    private final boolean aDouble;
    private final boolean aTriple;

    public static DartSegment from(int value) {
        int multiplier = value / 100;
        int field = value % 100;
        int score = multiplier * field;

        return Arrays.stream(values()).filter(s -> s.score == score).findFirst().orElseThrow();
    }
}
