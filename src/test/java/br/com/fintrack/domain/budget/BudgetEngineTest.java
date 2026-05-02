package br.com.fintrack.domain.budget;

import br.com.fintrack.domain.debt.entity.Debt;
import br.com.fintrack.domain.expense.entity.Expense;
import br.com.fintrack.domain.expense.entity.enums.ExpenseCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetEngineTest {

    private BudgetEngine engine;

    @BeforeEach
    void setUp() {
        engine = new BudgetEngine();
    }

    @Test
    void shouldReturnFullIncomeAsBalanceWhenNoExpensesOrDebts() {
        var income = new BigDecimal("5000.00");

        var result = engine.calculate(income, List.of(), List.of());

        assertThat(result.income()).isEqualByComparingTo("5000.00");
        assertThat(result.totalExpenses()).isEqualByComparingTo("0.00");
        assertThat(result.totalDebtInstallments()).isEqualByComparingTo("0.00");
        assertThat(result.balance()).isEqualByComparingTo("5000.00");
        assertThat(result.expensesByCategory()).isEmpty();
    }

    @Test
    void shouldCalculateTotalExpensesCorrectly() {
        var income = new BigDecimal("5000.00");
        var expenses = List.of(
                expense("Mercado", new BigDecimal("800.00"), ExpenseCategory.FOOD),
                expense("Aluguel", new BigDecimal("1500.00"), ExpenseCategory.HOUSING),
                expense("Transporte", new BigDecimal("300.00"), ExpenseCategory.TRANSPORT)
        );

        var result = engine.calculate(income, expenses, List.of());

        assertThat(result.totalExpenses()).isEqualByComparingTo("2600.00");
        assertThat(result.balance()).isEqualByComparingTo("2400.00");
    }

    @Test
    void shouldGroupExpensesByCategory() {
        var income = new BigDecimal("5000.00");
        var expenses = List.of(
                expense("Mercado", new BigDecimal("500.00"), ExpenseCategory.FOOD),
                expense("Restaurante", new BigDecimal("300.00"), ExpenseCategory.FOOD),
                expense("Aluguel", new BigDecimal("1500.00"), ExpenseCategory.HOUSING)
        );

        var result = engine.calculate(income, expenses, List.of());

        assertThat(result.expensesByCategory()).containsKey(ExpenseCategory.FOOD);
        assertThat(result.expensesByCategory().get(ExpenseCategory.FOOD)).isEqualByComparingTo("800.00");
        assertThat(result.expensesByCategory().get(ExpenseCategory.HOUSING)).isEqualByComparingTo("1500.00");
    }

    @Test
    void shouldCalculateDebtInstallmentsAsMonthlyInterest() {
        var income = new BigDecimal("5000.00");
        var debts = List.of(
                debt(new BigDecimal("10000.00"), new BigDecimal("2.00"))
        );

        var result = engine.calculate(income, List.of(), debts);

        assertThat(result.totalDebtInstallments()).isEqualByComparingTo("200.00");
        assertThat(result.balance()).isEqualByComparingTo("4800.00");
    }

    @Test
    void shouldCalculateNegativeBalanceWhenExpensesExceedIncome() {
        var income = new BigDecimal("2000.00");
        var expenses = List.of(
                expense("Aluguel", new BigDecimal("1500.00"), ExpenseCategory.HOUSING),
                expense("Mercado", new BigDecimal("800.00"), ExpenseCategory.FOOD)
        );

        var result = engine.calculate(income, expenses, List.of());

        assertThat(result.balance()).isNegative();
        assertThat(result.balance()).isEqualByComparingTo("-300.00");
    }

    @Test
    void shouldSumAllComponents() {
        var income = new BigDecimal("6000.00");
        var expenses = List.of(
                expense("Aluguel", new BigDecimal("1500.00"), ExpenseCategory.HOUSING),
                expense("Mercado", new BigDecimal("600.00"), ExpenseCategory.FOOD)
        );
        var debts = List.of(
                debt(new BigDecimal("5000.00"), new BigDecimal("1.00"))
        );

        var result = engine.calculate(income, expenses, debts);

        assertThat(result.totalExpenses()).isEqualByComparingTo("2100.00");
        assertThat(result.totalDebtInstallments()).isEqualByComparingTo("50.00");
        assertThat(result.balance()).isEqualByComparingTo("3850.00");
    }

    private Expense expense(String description, BigDecimal amount, ExpenseCategory category) {
        Expense expense = new Expense();
        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setActive(true);
        return expense;
    }

    private Debt debt(BigDecimal remainingAmount, BigDecimal interestRate) {
        Debt debt = new Debt();
        debt.setRemainingAmount(remainingAmount);
        debt.setInterestRate(interestRate);
        return debt;
    }
}
