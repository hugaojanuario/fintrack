package br.com.fintrack.domain.expense.repository;

import br.com.fintrack.domain.expense.entity.Expense;
import br.com.fintrack.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    Page<Expense> findByUserAndActiveTrue(User user, Pageable pageable);
    List<Expense> findByUserAndActiveTrue(User user);
    Optional<Expense> findByIdAndUser(UUID id, User user);
}
