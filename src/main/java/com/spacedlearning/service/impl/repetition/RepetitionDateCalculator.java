package com.spacedlearning.service.impl.repetition;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.spacedlearning.entity.Module;
import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.enums.CycleStudied;
import com.spacedlearning.entity.enums.RepetitionOrder;

@Component
public class RepetitionDateCalculator {
    private static final double BASE_DAILY_WORDS = 41.7;
    private static final double COMPLETION_ADJUSTMENT_FACTOR = 0.5;
    private static final EnumMap<RepetitionOrder, Integer> ORDER_INDEX_MAP = new EnumMap<>(RepetitionOrder.class);
    private static final int[] REVIEW_MULTIPLIERS = { 2, 4, 8, 13, 19, 26 };
    private static final double STUDY_CYCLE_ADJUSTMENT_FACTOR = 0.2;

    private static final double WORD_FACTOR_ADJUSTMENT = 0.3;
    static {
        final var values = RepetitionOrder.values();
        for (var i = 0; i < values.length; i++) {
            ORDER_INDEX_MAP.put(values[i], i);
        }
    }

    public LocalDate calculateAdjustedDate(ModuleProgress progress, int index, LocalDate baseDate,
            Map<LocalDate, Long> dateCounts) {
        final int wordCount = Optional.ofNullable(progress.getModule())
                .map(Module::getWordCount)
                .orElse(0);
        final var wordFactor = Math.max(20, wordCount) / BASE_DAILY_WORDS;

        final double completedPercent = Optional.ofNullable(progress.getPercentComplete())
                .map(BigDecimal::doubleValue)
                .orElse(0.0);
        final var cycleCount = getCycleStudiedCount(progress.getCyclesStudied());

        final var factor = 1.0
                + ((cycleCount - 1) * STUDY_CYCLE_ADJUSTMENT_FACTOR)
                + ((completedPercent / 100.0) * COMPLETION_ADJUSTMENT_FACTOR)
                + ((wordFactor - 1.0) * WORD_FACTOR_ADJUSTMENT);

        final var baseMultiplier = index < REVIEW_MULTIPLIERS.length
                ? REVIEW_MULTIPLIERS[index]
                : REVIEW_MULTIPLIERS[REVIEW_MULTIPLIERS.length - 1];

        final var dayOffset = Math.round(baseMultiplier * factor);
        return baseDate.plusDays(dayOffset);
    }

    public int getCycleStudiedCount(CycleStudied studied) {
        return switch (studied) {
        case FIRST_REVIEW -> 1;
        case SECOND_REVIEW -> 2;
        case THIRD_REVIEW, MORE_THAN_THREE_REVIEWS -> 3;
        default -> 0;
        };
    }

    public int getMinRequiredGap(int fromIndex, int toIndex) {
        if ((fromIndex >= REVIEW_MULTIPLIERS.length) || (toIndex >= REVIEW_MULTIPLIERS.length)) {
            throw new IllegalArgumentException("Invalid repetition index for multiplier array.");
        }
        return REVIEW_MULTIPLIERS[toIndex] - REVIEW_MULTIPLIERS[fromIndex];
    }

    public int getOrderIndex(RepetitionOrder order) {
        return ORDER_INDEX_MAP.getOrDefault(order, -1);
    }

    public boolean isFinalRepetition(int index) {
        return index >= (RepetitionOrder.values().length - 1);
    }
}
