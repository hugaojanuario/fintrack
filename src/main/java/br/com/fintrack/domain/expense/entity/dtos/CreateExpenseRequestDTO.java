package br.com.fintrack.domain.expense.entity.dtos;

import br.com.fintrack.domain.expense.entity.enums.ExpenseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateExpenseRequestDTO(
        @NotBlank String description,
        @NotNull BigDecimal amount,
        @NotNull ExpenseCategory category,
        Integer dueDay
) {
}
