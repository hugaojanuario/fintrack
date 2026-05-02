package br.com.fintrack.domain.debt.entity.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateDebtRequestDTO(
        @NotBlank String creditor,
        @NotBlank String description,
        @NotNull BigDecimal totalAmount,
        @NotNull BigDecimal interestRate,
        @NotNull Integer dueDay
) {
}
