package br.com.fintrack.domain.recommendation.strategy;

import br.com.fintrack.domain.user.entity.User;
import br.com.fintrack.domain.user.entity.enums.InvestorProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ModerateStrategyTest {

    private ModerateStrategy strategy;
    private User user;

    @BeforeEach
    void setUp() {
        strategy = new ModerateStrategy();
        user = mock(User.class);
    }

    @Test
    void shouldGenerateRecommendations() {
        var result = strategy.generate(user);

        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldAssignModerateProfile() {
        var result = strategy.generate(user);

        assertThat(result).allMatch(r -> r.getInvestorProfile() == InvestorProfile.MODERATE);
    }

    @Test
    void shouldSetUserOnAllRecommendations() {
        var result = strategy.generate(user);

        assertThat(result).allMatch(r -> r.getUser() == user);
    }

    @Test
    void shouldHaveTitleAndDescription() {
        var result = strategy.generate(user);

        assertThat(result).allMatch(r -> r.getTitle() != null && !r.getTitle().isBlank());
        assertThat(result).allMatch(r -> r.getDescription() != null && !r.getDescription().isBlank());
    }
}
