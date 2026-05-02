package br.com.fintrack.domain.recommendation.repository;

import br.com.fintrack.domain.recommendation.entity.Recommendation;
import br.com.fintrack.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {

    Page<Recommendation> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
