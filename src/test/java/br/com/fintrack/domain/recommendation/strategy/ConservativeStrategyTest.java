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
class ConservativeStrategyTest {

    private ConservativeStrategy strategy;
    private User user;

    @BeforeEach
    void setUp() {
        strategy = new ConservativeStrategy();
        user = mock(User.class);
    }

    @Test
    void shouldGenerateRecommendations() {
        var result = strategy.generate(user);

        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldReturnOnlyLowRiskRecommendations() {
        var result = strategy.generate(user);

        assertThat(result).allMatch(r -> r.getRiskLevel() == RiskLevel.LOW);
    }

    @Test
    void shouldAssignConservativeProfile() {
        var result = strategy.generate(user);

        assertThat(result).allMatch(r -> r.getInvestorProfile() == InvestorProfile.CONSERVATIVE);
    }

    @Test
    void shouldSetUserOnAllRecommendations() {
        var result = strategy.generate(user);

        assertThat(result).allMatch(r -> r.getUser() == user);
    }

    @Test
    void shouldHavePositiveExpectedReturn() {
        var result = strategy.generate(user);

        assertThat(result).allMatch(r -> r.getExpectedReturn().signum() > 0);
    }
}
