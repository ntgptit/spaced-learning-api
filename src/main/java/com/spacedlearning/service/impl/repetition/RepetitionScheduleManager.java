package com.spacedlearning.service.impl.repetition;

import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.CycleStudied;
import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;
import com.spacedlearning.repository.ModuleProgressRepository;
import com.spacedlearning.repository.RepetitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

//New class to handle scheduling logic
@Component
@RequiredArgsConstructor
@Slf4j
public class RepetitionScheduleManager {

    // Base parameters for spaced repetition algorithm
    private static final double BASE_DAILY_WORDS = 41.7;
    private static final int[] REVIEW_MULTIPLIERS = {2, 4, 8, 13, 19, 26};
    private static final int MAX_WORD_FACTOR = 3;
    private static final int MAX_INTERVAL_DAYS = 31;
    private static final int MIN_DAILY_WORDS = 20;

    private final RepetitionRepository repetitionRepository;
    private final ModuleProgressRepository progressRepository;

    public void initializeFirstLearningDate(ModuleProgress progress) {
        if (progress.getFirstLearningDate() == null) {
            progress.setFirstLearningDate(LocalDate.now());
            progressRepository.save(progress);
        }
    }

    public List<Repetition> createRepetitionsForProgress(ModuleProgress progress) {
        final List<Repetition> repetitions = new ArrayList<>();
        final RepetitionOrder[] orders = RepetitionOrder.values();

        // Tạo bốn lần lặp đầu tiên
        for (int i = 0; i < 4; i++) {
            final Repetition repetition = createRepetition(progress, orders[i], i);
            repetitions.add(repetition);
        }

        // Xử lý đặc biệt cho lần lặp thứ 5
        LocalDate reviewDate = calculateReviewDate(progress, 4);

        // Kiểm tra xem có trùng với repetition 4 không
        if (repetitions.size() >= 4) {
            final LocalDate rep4Date = repetitions.get(3).getReviewDate();

            // Nếu trùng, thêm một khoảng cách cố định
            if (reviewDate.equals(rep4Date)) {
                // Thêm 10 ngày để tách biệt
                reviewDate = rep4Date.plusDays(15);
            }
        }

        final Repetition repetition5 = new Repetition();
        repetition5.setModuleProgress(progress);
        repetition5.setRepetitionOrder(orders[4]);
        repetition5.setStatus(RepetitionStatus.NOT_STARTED);
        repetition5.setReviewDate(findOptimalDate(reviewDate));
        repetitions.add(repetition5);

        return repetitions;
    }

    /**
     * Reschedule future repetitions based on a new start date for a specific repetition
     *
     * @param progress     Progress record
     * @param currentOrder Current repetition order
     * @param newStartDate New start date for the current repetition
     */
    public void rescheduleFutureRepetitions(
            ModuleProgress progress,
            RepetitionOrder currentOrder,
            LocalDate newStartDate) {
        final int currentIndex = getOrderIndex(currentOrder);
        if (currentIndex == -1 || currentIndex >= RepetitionOrder.values().length - 1) {
            return;
        }

        final List<Repetition> futureRepetitions = getFutureRepetitions(progress, currentIndex);

        if (futureRepetitions.isEmpty()) {
            log.debug("No future repetitions to reschedule for progress ID: {}", progress.getId());
            return;
        }

        log.info("Rescheduling {} future repetitions based on new date: {}", Optional.of(futureRepetitions.size()),
                newStartDate);

        // Calculate intervals relative to the current repetition
        RepetitionOrder.values();

        for (final Repetition repetition : futureRepetitions) {
            final int repIndex = getOrderIndex(repetition.getRepetitionOrder());
            Math.max(1, repIndex - currentIndex);

            // Calculate new review date based on spaced repetition intervals
            // Use the review multipliers to maintain proper spacing
            final int dayOffset = REVIEW_MULTIPLIERS[repIndex] - REVIEW_MULTIPLIERS[currentIndex];
            final LocalDate newReviewDate = newStartDate.plusDays(dayOffset);

            repetition.setReviewDate(newReviewDate);
            log.debug("Rescheduled repetition {} to {}", repetition.getRepetitionOrder(), newReviewDate);
        }

        repetitionRepository.saveAll(futureRepetitions);
        updateNextStudyDate(progress);
    }

    private Repetition createRepetition(ModuleProgress progress, RepetitionOrder order, int reviewIndex) {
        final LocalDate reviewDate = calculateReviewDate(progress, reviewIndex);
        final Repetition repetition = new Repetition();
        repetition.setModuleProgress(progress);
        repetition.setRepetitionOrder(order);
        repetition.setStatus(RepetitionStatus.NOT_STARTED);
        repetition.setReviewDate(reviewDate);
        return repetition;
    }

    public LocalDate calculateReviewDate(ModuleProgress progress, int reviewIndex) {
        final Integer wordCount = progress.getModule() != null ? progress.getModule().getWordCount() : (Integer) 0;
        final double dailyWordCount = Math.max(MIN_DAILY_WORDS, wordCount > 0 ? wordCount : BASE_DAILY_WORDS);
        final int studyCycleCount = getStudyCycleCount(progress.getCyclesStudied());
        final BigDecimal percentComplete = progress.getPercentComplete();

        final int adjustedCycleCount = Math.max(1, studyCycleCount);
        final double wordFactor = calculateWordFactor(dailyWordCount);
        final double baseInterval = calculateBaseInterval(reviewIndex, adjustedCycleCount, wordFactor);
        final LocalDate referenceDate = progress.getFirstLearningDate() != null ? progress.getFirstLearningDate()
                : LocalDate.now();

        final double daysToAdd = percentComplete == null || percentComplete.doubleValue() == 0.0
                ? baseInterval
                : baseInterval * percentComplete.doubleValue() / 100.0;

        final LocalDate initialDate = referenceDate.plusDays(Math.round(daysToAdd));
        final LocalDate constrainedDate = constrainDate(progress, initialDate);
        return findOptimalDate(constrainedDate);
    }

    private LocalDate constrainDate(ModuleProgress progress, LocalDate initialDate) {
        if (progress.getFirstLearningDate() != null && initialDate.minusDays(60).isAfter(progress
                .getFirstLearningDate())) {
            return progress.getFirstLearningDate().plusDays(60);
        }
        return initialDate;
    }

    private LocalDate findOptimalDate(LocalDate initialDate) {
        if (initialDate == null) {
            throw new IllegalArgumentException("Initial date cannot be null");
        }

        final LocalDate endDate = initialDate.plusDays(7);

        final List<Object[]> dateCountsList = repetitionRepository.countReviewDatesBetween(initialDate, endDate);
        if (dateCountsList == null || dateCountsList.isEmpty()) {
            log.warn("No repetition counts available for range {} to {}. Using initial date.", initialDate, endDate);
            return initialDate;
        }

        final Map<LocalDate, Long> dateCounts = dateCountsList.stream()
                .collect(Collectors.toMap(
                        arr -> {
                            final Object dateObj = arr[0];
                            if (dateObj instanceof LocalDate) {
                                return (LocalDate) dateObj;
                            }
                            if (dateObj instanceof java.sql.Date) {
                                return ((java.sql.Date) dateObj).toLocalDate();
                            }
                            throw new IllegalStateException("Unexpected reviewDate type: " + dateObj.getClass());
                        },
                        arr -> ((Number) arr[1]).longValue(),
                        (v1, v2) -> v1));

        final Long initialCount = dateCounts.getOrDefault(initialDate, 0L);
        if (initialCount <= 3) {
            return initialDate;
        }

        log.warn("Initial date {} is overloaded (count={}). Searching alternatives...", initialDate, initialCount);
        for (int i = 1; i <= 7; i++) {
            final LocalDate candidateDate = initialDate.plusDays(i);
            if (candidateDate.isBefore(LocalDate.now())) {
                continue; // Bỏ qua ngày trong quá khứ
            }
            final Long count = dateCounts.getOrDefault(candidateDate, 0L);
            if (count <= 3) {
                log.info("Found suitable alternative date: {}", candidateDate);
                return candidateDate;
            }
        }

        final LocalDate fallbackDate = initialDate.plusDays(1);
        log.warn("No suitable alternative found within 7 days (all dates have count > 3). Falling back to: {}",
                fallbackDate);
        return fallbackDate;
    }

    private double calculateWordFactor(double dailyWordCount) {
        return Math.min(MAX_WORD_FACTOR, Math.max(dailyWordCount, BASE_DAILY_WORDS) / BASE_DAILY_WORDS);
    }

    private double calculateBaseInterval(int reviewIndex, int adjustedCycleCount, double wordFactor) {
        final int baseMultiplier = reviewIndex < REVIEW_MULTIPLIERS.length ? REVIEW_MULTIPLIERS[reviewIndex]
                : REVIEW_MULTIPLIERS[REVIEW_MULTIPLIERS.length - 1];
        if (adjustedCycleCount >= 3 && reviewIndex >= 2) {
            return Math.sqrt(adjustedCycleCount * baseMultiplier) * wordFactor * 5;
        }
        return wordFactor * Math.min(MAX_INTERVAL_DAYS, adjustedCycleCount * baseMultiplier);
    }

    public void updateNextStudyDate(ModuleProgress progress) {
        final List<Repetition> pendingRepetitions = repetitionRepository
                .findByModuleProgressIdAndStatusOrderByReviewDate(progress.getId(), RepetitionStatus.NOT_STARTED);
        if (!pendingRepetitions.isEmpty()) {
            progress.setNextStudyDate(pendingRepetitions.get(0).getReviewDate());
            progressRepository.save(progress);
            log.debug("Updated next study date to {} for progress ID: {}", progress.getNextStudyDate(), progress
                    .getId());
        }
    }

    public void updateFutureRepetitions(ModuleProgress progress, RepetitionOrder currentOrder) {
        final int currentIndex = getOrderIndex(currentOrder);
        if (currentIndex == -1 || currentIndex >= RepetitionOrder.values().length - 1) {
            return;
        }

        final List<Repetition> futureRepetitions = getFutureRepetitions(progress, currentIndex);
        updateRepetitionDates(progress, futureRepetitions);
    }

    private List<Repetition> getFutureRepetitions(ModuleProgress progress, int currentIndex) {
        final List<Repetition> futureRepetitions = repetitionRepository
                .findByModuleProgressIdAndStatusOrderByRepetitionOrder(progress.getId(), RepetitionStatus.NOT_STARTED);
        return futureRepetitions.stream()
                .filter(rep -> getOrderIndex(rep.getRepetitionOrder()) > currentIndex)
                .toList();
    }

    private void updateRepetitionDates(ModuleProgress progress, List<Repetition> repetitions) {
        if (repetitions.isEmpty()) {
            log.debug("No future repetitions to update for progress ID: {}", progress.getId());
            return;
        }

        for (final Repetition repetition : repetitions) {
            final int repetitionIndex = getOrderIndex(repetition.getRepetitionOrder());
            final LocalDate newReviewDate = calculateReviewDate(progress, repetitionIndex);
            repetition.setReviewDate(newReviewDate);
        }
        repetitionRepository.saveAll(repetitions);
    }

    private int getOrderIndex(RepetitionOrder order) {
        return Arrays.asList(RepetitionOrder.values()).indexOf(order);
    }

    public void checkAndUpdateCycleStudied(ModuleProgress progress) {
        final long totalRepetitionCount = repetitionRepository.countByModuleProgressId(progress.getId());
        final long completedCount = repetitionRepository.countByModuleProgressIdAndStatus(progress.getId(),
                RepetitionStatus.COMPLETED);

        if (completedCount >= totalRepetitionCount) {
            updateToNextCycleLevel(progress);
            createNewRepetitionCycle(progress);
        }
    }

    private void updateToNextCycleLevel(ModuleProgress progress) {
        final CycleStudied currentCycle = progress.getCyclesStudied();
        final CycleStudied nextCycle = getNextCycle(currentCycle);
        if (nextCycle != currentCycle) {
            progress.setCyclesStudied(nextCycle);
            progressRepository.save(progress);
            log.info("Updated cycle studied to {} for progress ID: {}", nextCycle, progress.getId());
        }
    }

    private CycleStudied getNextCycle(CycleStudied current) {
        return switch (current) {
            case FIRST_TIME -> CycleStudied.FIRST_REVIEW;
            case FIRST_REVIEW -> CycleStudied.SECOND_REVIEW;
            case SECOND_REVIEW -> CycleStudied.THIRD_REVIEW;
            case THIRD_REVIEW -> CycleStudied.MORE_THAN_THREE_REVIEWS;
            default -> current;
        };
    }

    private void createNewRepetitionCycle(ModuleProgress progress) {
        markExistingRepetitionsAsCompleted(progress);
        final List<Repetition> newRepetitions = createRepetitionsWithCurrentDate(progress);
        repetitionRepository.saveAll(newRepetitions);
        updateNextStudyDate(progress);
    }

    private void markExistingRepetitionsAsCompleted(ModuleProgress progress) {
        final List<Repetition> existingRepetitions = repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(
                progress.getId());
        existingRepetitions.stream()
                .filter(rep -> rep.getStatus() != RepetitionStatus.COMPLETED)
                .forEach(rep -> rep.setStatus(RepetitionStatus.COMPLETED));
        repetitionRepository.saveAll(existingRepetitions);
    }

    private List<Repetition> createRepetitionsWithCurrentDate(ModuleProgress progress) {
        final Optional<LocalDate> lastCompletedDate = repetitionRepository.findLastCompletedRepetitionDate(progress
                .getId());
        if (lastCompletedDate.isPresent()) {
            progress.setFirstLearningDate(lastCompletedDate.get().plusDays(1));
            progress.setNextStudyDate(calculateReviewDate(progress, 0));
            progressRepository.save(progress);
        }
        return createRepetitionsForProgress(progress);
    }

    private int getStudyCycleCount(CycleStudied cyclesStudied) {
        if (cyclesStudied == null) {
            return 0;
        }
        return switch (cyclesStudied) {
            case FIRST_REVIEW -> 1;
            case SECOND_REVIEW -> 2;
            case THIRD_REVIEW, MORE_THAN_THREE_REVIEWS -> 3;
            default -> 0;
        };
    }
}