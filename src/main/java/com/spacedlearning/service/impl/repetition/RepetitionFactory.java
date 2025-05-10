package com.spacedlearning.service.impl.repetition;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RepetitionFactory {
    private final RepetitionDateCalculator calculator;
    private final RepetitionDateOptimizer optimizer;

    public List<Repetition> generateSchedule(ModuleProgress progress, LocalDate baseDate,
            Map<LocalDate, Long> dateCounts) {
        final List<Repetition> result = new ArrayList<>();
        final var orders = RepetitionOrder.values();
        if (orders.length < 5) {
            return result;
        }

        LocalDate prevDate = null;
        var prevIndex = -1;

        for (var i = 0; i < 5; i++) {
            var rawDate = this.calculator.calculateAdjustedDate(progress, i, baseDate, dateCounts);
            if (prevDate != null) {
                final var minGap = this.calculator.getMinRequiredGap(prevIndex, i);
                rawDate = rawDate.isBefore(prevDate.plusDays(minGap)) ? prevDate.plusDays(minGap) : rawDate;
            }
            final var optimal = this.optimizer.findOptimalDate(rawDate, dateCounts);
            final var rep = new Repetition();
            rep.setModuleProgress(progress);
            rep.setRepetitionOrder(orders[i]);
            rep.setStatus(RepetitionStatus.NOT_STARTED);
            rep.setReviewDate(optimal);

            result.add(rep);
            prevDate = optimal;
            prevIndex = i;
        }
        return result;
    }
}
