package br.com.fintrack.domain.recommendation.service;

import br.com.fintrack.domain.recommendation.entity.dtos.RecommendationResponseDTO;
import br.com.fintrack.domain.recommendation.repository.RecommendationRepository;
import br.com.fintrack.domain.recommendation.strategy.InvestmentStrategy;
import br.com.fintrack.domain.user.entity.enums.InvestorProfile;
import br.com.fintrack.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository repository;
    private final UserRepository userRepository;
    private final Map<InvestorProfile, InvestmentStrategy> strategies;

    public Page<RecommendationResponseDTO> getAll(String userEmail, Pageable pageable) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        return repository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(RecommendationResponseDTO::new);
    }

    public List<RecommendationResponseDTO> generate(String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        var strategy = strategies.get(user.getInvestorProfile());

        var recommendations = strategy.generate(user);

        var saved = repository.saveAll(recommendations);

        return saved.stream().map(RecommendationResponseDTO::new).toList();
    }
}
