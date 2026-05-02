package br.com.fintrack.domain.goal.entity.dtos;

import br.com.fintrack.domain.goal.entity.Goal;
import br.com.fintrack.domain.goal.entity.enums.GoalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record GoalResponseDTO(
        UUID id,
        String title,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        LocalDate deadline,
        GoalStatus status,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        BigDecimal monthlyContributionNeeded,
        boolean onTrack,
        BigDecimal additionalMonthlyNeeded) {

    public GoalResponseDTO(Goal goal, BigDecimal monthlyContributionNeeded, boolean onTrack, BigDecimal additionalMonthlyNeeded) {
        this(
                goal.getId(),
                goal.getTitle(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.getDeadline(),
                goal.getStatus(),
                goal.isActive(),
                goal.getCreatedAt(),
                goal.getUpdatedAt(),
                monthlyContributionNeeded,
                onTrack,
                additionalMonthlyNeeded
        );
    }
}
