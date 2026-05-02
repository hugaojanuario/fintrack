package br.com.fintrack.domain.budget.repository;

import br.com.fintrack.domain.budget.entity.MonthlyBudget;
import br.com.fintrack.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MonthlyBudgetRepository extends JpaRepository<MonthlyBudget, UUID> {

    Page<MonthlyBudget> findByUserOrderByReferenceMonthDesc(User user, Pageable pageable);
}
