package br.com.fintrack.domain.recommendation.strategy;

import br.com.fintrack.domain.recommendation.entity.enums.RiskLevel;
import br.com.fintrack.domain.user.entity.User;
import br.com.fintrack.domain.user.entity.enums.InvestorProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AggressiveStrategyTest {

    private AggressiveStrategy strategy;
    private User user;

    @BeforeEach
    void setUp() {
        strategy = new AggressiveStrategy();
        user = mock(User.class);
    }

    @Test
    void shouldGenerateRecommendations() {
        var result = strategy.generate(user);

        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldReturnOnlyHighRiskRecommendations() {
        var result = strategy.generate(user);

        assertThat(result).allMatch(r -> r.getRiskLevel() == RiskLevel.HIGH);
    }

    @Test
    void shouldAssignAggressiveProfile() {
        var result = strategy.generate(user);

        assertThat(result).allMatch(r -> r.getInvestorProfile() == InvestorProfile.AGGRESSIVE);
    }

    @Test
    void shouldHaveHigherExpectedReturnThanConservative() {
        var result = strategy.generate(user);

        assertThat(result).allMatch(r -> r.getExpectedReturn().compareTo(new java.math.BigDecimal("10")) > 0);
    }
}
