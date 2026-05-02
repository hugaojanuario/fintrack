package br.com.fintrack.domain.budget.entity.dtos;

import br.com.fintrack.domain.expense.entity.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.util.Map;

public record BudgetSummaryDTO(
        BigDecimal income,
        BigDecimal totalExpenses,
        BigDecimal totalDebtInstallments,
        BigDecimal balance,
        Map<ExpenseCategory, BigDecimal> expensesByCategory) {
}
