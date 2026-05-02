package br.com.fintrack.domain.budget.entity.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record DebtProjectionDTO(
        UUID debtId,
        String creditor,
        BigDecimal remainingAmount,
        BigDecimal interestRate,
        Integer monthsToPayoff,
        LocalDate estimatedPayoffDate) {
}
