package br.com.fintrack.domain.budget.service;

import br.com.fintrack.domain.budget.BudgetEngine;
import br.com.fintrack.domain.budget.entity.MonthlyBudget;
import br.com.fintrack.domain.budget.entity.dtos.BudgetSummaryDTO;
import br.com.fintrack.domain.budget.entity.dtos.DebtProjectionDTO;
import br.com.fintrack.domain.budget.entity.dtos.GoalProjectionDTO;
import br.com.fintrack.domain.budget.entity.enums.DebtPaymentStrategy;
import br.com.fintrack.domain.budget.repository.MonthlyBudgetRepository;
import br.com.fintrack.domain.debt.entity.Debt;
import br.com.fintrack.domain.debt.entity.enums.DebtStatus;
import br.com.fintrack.domain.debt.repository.DebtRepository;
import br.com.fintrack.domain.expense.repository.ExpenseRepository;
import br.com.fintrack.domain.goal.entity.Goal;
import br.com.fintrack.domain.goal.entity.enums.GoalStatus;
import br.com.fintrack.domain.goal.repository.GoalRepository;
import br.com.fintrack.domain.user.entity.User;
import br.com.fintrack.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final MonthlyBudgetRepository monthlyBudgetRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final DebtRepository debtRepository;
    private final GoalRepository goalRepository;

    public BudgetSummaryDTO getSummary(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        var expenses = expenseRepository.findByUserAndActiveTrue(user);
        var debts = debtRepository.findByUserAndActiveTrueAndStatus(user, DebtStatus.ACTIVE);

        return new BudgetEngine().calculate(user.getMonthlyIncome(), expenses, debts);
    }

    public Page<MonthlyBudget> getHistory(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        return monthlyBudgetRepository.findByUserOrderByReferenceMonthDesc(user, pageable);
    }

    public List<DebtProjectionDTO> getDebtProjection(String userEmail, DebtPaymentStrategy strategy) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        var expenses = expenseRepository.findByUserAndActiveTrue(user);
        var debts = debtRepository.findByUserAndActiveTrueAndStatus(user, DebtStatus.ACTIVE);

        var totalExpenses = expenses.stream()
                .map(e -> e.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var monthlyBudget = user.getMonthlyIncome()
                .subtract(totalExpenses)
                .setScale(2, RoundingMode.HALF_UP);

        return simulateDebtPayoff(debts, monthlyBudget, strategy);
    }

    public List<GoalProjectionDTO> getGoalsProjection(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        var expenses = expenseRepository.findByUserAndActiveTrue(user);
        var debts = debtRepository.findByUserAndActiveTrueAndStatus(user, DebtStatus.ACTIVE);
        var goals = goalRepository.findByUserAndActiveTrueAndStatus(user, GoalStatus.ACTIVE);

        var totalExpenses = expenses.stream()
                .map(e -> e.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var totalDebtInstallments = debts.stream()
                .map(debt -> debt.getRemainingAmount()
                        .multiply(debt.getInterestRate()
                                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP))
                        .setScale(2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var availableForGoals = user.getMonthlyIncome()
                .subtract(totalExpenses)
                .subtract(totalDebtInstallments)
                .setScale(2, RoundingMode.HALF_UP);

        var perGoalMonthly = goals.isEmpty() ? BigDecimal.ZERO
                : availableForGoals.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO
                : availableForGoals.divide(BigDecimal.valueOf(goals.size()), 2, RoundingMode.HALF_UP);

        return goals.stream().map(goal -> buildGoalProjection(goal, perGoalMonthly)).toList();
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void saveMonthlySnapshot() {
        var users = userRepository.findAll();

        for (var user : users) {
            var expenses = expenseRepository.findByUserAndActiveTrue(user);
            var debts = debtRepository.findByUserAndActiveTrueAndStatus(user, DebtStatus.ACTIVE);
            var summary = new BudgetEngine().calculate(user.getMonthlyIncome(), expenses, debts);

            MonthlyBudget snapshot = new MonthlyBudget();
            snapshot.setUser(user);
            snapshot.setReferenceMonth(LocalDate.now().withDayOfMonth(1));
            snapshot.setIncome(summary.income());
            snapshot.setTotalExpenses(summary.totalExpenses());
            snapshot.setTotalDebtInstallments(summary.totalDebtInstallments());
            snapshot.setBalance(summary.balance());

            monthlyBudgetRepository.save(snapshot);
        }
    }

    private List<DebtProjectionDTO> simulateDebtPayoff(List<Debt> debts, BigDecimal monthlyBudget, DebtPaymentStrategy strategy) {
        if (monthlyBudget.compareTo(BigDecimal.ZERO) <= 0) {
            return debts.stream()
                    .map(d -> new DebtProjectionDTO(d.getId(), d.getCreditor(), d.getRemainingAmount(), d.getInterestRate(), null, null))
                    .toList();
        }

        List<Debt> sorted = strategy == DebtPaymentStrategy.SNOWBALL
                ? debts.stream().sorted(Comparator.comparing(Debt::getRemainingAmount)).toList()
                : debts.stream().sorted(Comparator.comparing(Debt::getInterestRate).reversed()).toList();

        var balances = new LinkedHashMap<UUID, BigDecimal>();
        for (var debt : sorted) {
            balances.put(debt.getId(), debt.getRemainingAmount().setScale(2, RoundingMode.HALF_UP));
        }

        var payoffMonths = new HashMap<UUID, Integer>();

        for (int month = 1; month <= 360; month++) {
            for (var debt : sorted) {
                if (!balances.containsKey(debt.getId())) continue;
                var balance = balances.get(debt.getId());
                var interest = balance.multiply(debt.getInterestRate()
                        .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP))
                        .setScale(2, RoundingMode.HALF_UP);
                balances.put(debt.getId(), balance.add(interest).setScale(2, RoundingMode.HALF_UP));
            }

            var remaining = monthlyBudget;
            for (var debt : sorted) {
                if (!balances.containsKey(debt.getId())) continue;
                var balance = balances.get(debt.getId());
                var payment = remaining.min(balance);
                var newBalance = balance.subtract(payment).setScale(2, RoundingMode.HALF_UP);
                remaining = remaining.subtract(payment).setScale(2, RoundingMode.HALF_UP);

                if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
                    balances.remove(debt.getId());
                    payoffMonths.put(debt.getId(), month);
                } else {
                    balances.put(debt.getId(), newBalance);
                }

                if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            }

            if (balances.isEmpty()) break;
        }

        var now = LocalDate.now();
        return sorted.stream().map(debt -> {
            var months = payoffMonths.get(debt.getId());
            var date = months != null ? now.plusMonths(months) : null;
            return new DebtProjectionDTO(debt.getId(), debt.getCreditor(), debt.getRemainingAmount(), debt.getInterestRate(), months, date);
        }).toList();
    }

    private GoalProjectionDTO buildGoalProjection(Goal goal, BigDecimal perGoalMonthly) {
        var remaining = goal.getTargetAmount()
                .subtract(goal.getCurrentAmount())
                .setScale(2, RoundingMode.HALF_UP);

        var monthsRemaining = ChronoUnit.MONTHS.between(LocalDate.now(), goal.getDeadline());

        BigDecimal monthlyContributionNeeded;
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            monthlyContributionNeeded = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        } else if (monthsRemaining <= 0) {
            monthlyContributionNeeded = remaining;
        } else {
            monthlyContributionNeeded = remaining.divide(BigDecimal.valueOf(monthsRemaining), 2, RoundingMode.HALF_UP);
        }

        LocalDate estimatedCompletionDate = null;
        if (perGoalMonthly.compareTo(BigDecimal.ZERO) > 0 && remaining.compareTo(BigDecimal.ZERO) > 0) {
            var months = remaining.divide(perGoalMonthly, 0, RoundingMode.CEILING).longValue();
            estimatedCompletionDate = LocalDate.now().plusMonths(months);
        }

        var totalMonths = ChronoUnit.MONTHS.between(goal.getCreatedAt().toLocalDate(), goal.getDeadline());
        boolean onTrack;
        if (totalMonths <= 0) {
            onTrack = goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0;
        } else {
            var elapsedMonths = ChronoUnit.MONTHS.between(goal.getCreatedAt().toLocalDate(), LocalDate.now());
            var expectedProgress = BigDecimal.valueOf(elapsedMonths).divide(BigDecimal.valueOf(totalMonths), 4, RoundingMode.HALF_UP);
            var actualProgress = goal.getCurrentAmount().divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP);
            onTrack = actualProgress.compareTo(expectedProgress) >= 0;
        }

        return new GoalProjectionDTO(
                goal.getId(),
                goal.getTitle(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.getDeadline(),
                monthlyContributionNeeded,
                estimatedCompletionDate,
                onTrack
        );
    }
}
