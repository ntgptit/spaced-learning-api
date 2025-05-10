package com.spacedlearning.service.impl.repetition;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class RepetitionDateOptimizer {
    private static final long MAX_COUNT = 3;
    private static final int SEARCH_WINDOW_DAYS = 7;

    public LocalDate findOptimalDate(LocalDate proposedDate, Map<LocalDate, Long> dateCounts) {
        final var today = LocalDate.now();
        if (proposedDate.isBefore(today)) {
            return today;
        }

        if (dateCounts.getOrDefault(proposedDate, 0L) <= RepetitionDateOptimizer.MAX_COUNT) {
            return proposedDate;
        }

        for (var i = 1; i <= RepetitionDateOptimizer.SEARCH_WINDOW_DAYS; i++) {
            final var candidate = proposedDate.plusDays(i);
            if (!candidate.isBefore(today) && (dateCounts.getOrDefault(candidate,
                    0L) <= RepetitionDateOptimizer.MAX_COUNT)) {
                return candidate;
            }
        }
        return proposedDate.plusDays(1).isBefore(today) ? today : proposedDate.plusDays(1);
    }
}
