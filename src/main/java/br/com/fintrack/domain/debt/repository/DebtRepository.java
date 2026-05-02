package br.com.fintrack.domain.debt.repository;

import br.com.fintrack.domain.debt.entity.Debt;
import br.com.fintrack.domain.debt.entity.enums.DebtStatus;
import br.com.fintrack.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DebtRepository extends JpaRepository<Debt, UUID> {
    Page<Debt> findByUserAndActiveTrue(User user, Pageable pageable);
    List<Debt> findByUserAndActiveTrueAndStatus(User user, DebtStatus status);
    Optional<Debt> findByIdAndUser(UUID id, User user);
}
