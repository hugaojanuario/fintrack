package br.com.fintrack.domain.recommendation.strategy;

import br.com.fintrack.domain.recommendation.entity.Recommendation;
import br.com.fintrack.domain.user.entity.User;

import java.util.List;

public interface InvestmentStrategy {

    List<Recommendation> generate(User user);
}
