package br.com.fintrack.domain.budget;

import br.com.fintrack.domain.budget.entity.dtos.BudgetSummaryDTO;
import br.com.fintrack.domain.debt.entity.Debt;
import br.com.fintrack.domain.expense.entity.Expense;
import br.com.fintrack.domain.expense.entity.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BudgetEngine {

    public BudgetSummaryDTO calculate(BigDecimal income, List<Expense> expenses, List<Debt> debts) {
        Map<ExpenseCategory, BigDecimal> expensesByCategory = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                ));

        var totalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        var totalDebtInstallments = debts.stream()
                .map(debt -> debt.getRemainingAmount()
                        .multiply(debt.getInterestRate()
                                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP))
                        .setScale(2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        var balance = income
                .subtract(totalExpenses)
                .subtract(totalDebtInstallments)
                .setScale(2, RoundingMode.HALF_UP);

        return new BudgetSummaryDTO(income, totalExpenses, totalDebtInstallments, balance, expensesByCategory);
    }
}
