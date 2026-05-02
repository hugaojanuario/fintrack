package br.com.fintrack.domain.recommendation.config;

import br.com.fintrack.domain.recommendation.strategy.AggressiveStrategy;
import br.com.fintrack.domain.recommendation.strategy.ConservativeStrategy;
import br.com.fintrack.domain.recommendation.strategy.InvestmentStrategy;
import br.com.fintrack.domain.recommendation.strategy.ModerateStrategy;
import br.com.fintrack.domain.user.entity.enums.InvestorProfile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RecommendationConfig {

    @Bean
    public Map<InvestorProfile, InvestmentStrategy> strategies(ConservativeStrategy conservative,
                                                                ModerateStrategy moderate,
                                                                AggressiveStrategy aggressive) {
        return Map.of(
                InvestorProfile.CONSERVATIVE, conservative,
                InvestorProfile.MODERATE, moderate,
                InvestorProfile.AGGRESSIVE, aggressive
        );
    }
}
