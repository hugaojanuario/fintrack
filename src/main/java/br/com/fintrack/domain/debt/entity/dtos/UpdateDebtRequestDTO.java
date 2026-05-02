package br.com.fintrack.domain.debt.entity.dtos;

import java.math.BigDecimal;

public record UpdateDebtRequestDTO(
        String creditor,
        String description,
        BigDecimal interestRate,
        Integer dueDay
) {
}
