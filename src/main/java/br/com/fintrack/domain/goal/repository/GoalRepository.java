package br.com.fintrack.domain.goal.repository;

import br.com.fintrack.domain.goal.entity.Goal;
import br.com.fintrack.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {

    Page<Goal> findByUserAndActiveTrue(User user, Pageable pageable);

    Optional<Goal> findByIdAndUser(UUID id, User user);
}