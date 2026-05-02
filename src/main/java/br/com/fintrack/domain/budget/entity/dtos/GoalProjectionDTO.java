package br.com.fintrack.domain.budget.entity.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record GoalProjectionDTO(
        UUID goalId,
        String title,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        LocalDate deadline,
        BigDecimal monthlyContributionNeeded,
        LocalDate estimatedCompletionDate,
        boolean onTrack) {
}
