package br.com.fintrack.domain.expense.entity.dtos;

import br.com.fintrack.domain.expense.entity.enums.ExpenseCategory;

import java.math.BigDecimal;

public record UpdateExpenseRequestDTO(
        String description,
        BigDecimal amount,
        ExpenseCategory category,
        Integer dueDay
) {
}
