package br.com.fintrack.domain.recommendation.strategy;

import br.com.fintrack.domain.recommendation.entity.Recommendation;
import br.com.fintrack.domain.recommendation.entity.enums.AssetType;
import br.com.fintrack.domain.recommendation.entity.enums.RiskLevel;
import br.com.fintrack.domain.user.entity.User;
import br.com.fintrack.domain.user.entity.enums.InvestorProfile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ModerateStrategy implements InvestmentStrategy {

    @Override
    public List<Recommendation> generate(User user) {
        var r1 = new Recommendation();
        r1.setUser(user);
        r1.setInvestorProfile(InvestorProfile.MODERATE);
        r1.setTitle("FII HGLG11 — Logística");
        r1.setDescription("Fundo imobiliário de galpões logísticos com histórico sólido de distribuição de dividendos. Exposição ao setor imobiliário com liquidez de bolsa.");
        r1.setAssetType(AssetType.REAL_ESTATE_FUND);
        r1.setExpectedReturn(new BigDecimal("12.00"));
        r1.setRiskLevel(RiskLevel.MEDIUM);

        var r2 = new Recommendation();
        r2.setUser(user);
        r2.setInvestorProfile(InvestorProfile.MODERATE);
        r2.setTitle("VALE3 — Mineração");
        r2.setDescription("Ação da maior mineradora do Brasil e uma das maiores do mundo. Exposição a commodities com bom histórico de dividendos.");
        r2.setAssetType(AssetType.STOCKS);
        r2.setExpectedReturn(new BigDecimal("15.00"));
        r2.setRiskLevel(RiskLevel.MEDIUM);

        var r3 = new Recommendation();
        r3.setUser(user);
        r3.setInvestorProfile(InvestorProfile.MODERATE);
        r3.setTitle("LCI Isenta de IR");
        r3.setDescription("Letra de Crédito Imobiliário com isenção de imposto de renda para pessoa física. Rentabilidade líquida competitiva com risco moderado.");
        r3.setAssetType(AssetType.FIXED_INCOME);
        r3.setExpectedReturn(new BigDecimal("9.80"));
        r3.setRiskLevel(RiskLevel.LOW);

        var r4 = new Recommendation();
        r4.setUser(user);
        r4.setInvestorProfile(InvestorProfile.MODERATE);
        r4.setTitle("WEGE3 — Indústria");
        r4.setDescription("Ação da WEG, empresa industrial com presença global e crescimento consistente. Excelente histórico de resultados e geração de caixa.");
        r4.setAssetType(AssetType.STOCKS);
        r4.setExpectedReturn(new BigDecimal("18.00"));
        r4.setRiskLevel(RiskLevel.MEDIUM);

        return List.of(r1, r2, r3, r4);
    }
}
