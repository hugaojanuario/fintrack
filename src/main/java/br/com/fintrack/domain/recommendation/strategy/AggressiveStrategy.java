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
public class AggressiveStrategy implements InvestmentStrategy {

    @Override
    public List<Recommendation> generate(User user) {
        var r1 = new Recommendation();
        r1.setUser(user);
        r1.setInvestorProfile(InvestorProfile.AGGRESSIVE);
        r1.setTitle("PETR4 — Petróleo");
        r1.setDescription("Ação da Petrobras com alta liquidez e exposição ao preço do petróleo. Perfil especulativo com forte geração de dividendos em ciclos favoráveis.");
        r1.setAssetType(AssetType.STOCKS);
        r1.setExpectedReturn(new BigDecimal("22.00"));
        r1.setRiskLevel(RiskLevel.HIGH);

        var r2 = new Recommendation();
        r2.setUser(user);
        r2.setInvestorProfile(InvestorProfile.AGGRESSIVE);
        r2.setTitle("Bitcoin (BTC)");
        r2.setDescription("Principal criptomoeda do mercado. Alta volatilidade com potencial de retorno expressivo no longo prazo. Recomenda-se alocação de no máximo 5% do patrimônio.");
        r2.setAssetType(AssetType.CRYPTO);
        r2.setExpectedReturn(new BigDecimal("40.00"));
        r2.setRiskLevel(RiskLevel.HIGH);

        var r3 = new Recommendation();
        r3.setUser(user);
        r3.setInvestorProfile(InvestorProfile.AGGRESSIVE);
        r3.setTitle("RENT3 — Locação de Veículos");
        r3.setDescription("Ação da Localiza, líder em locação de veículos no Brasil. Setor em expansão com modelo de negócio resiliente e crescimento de longo prazo.");
        r3.setAssetType(AssetType.STOCKS);
        r3.setExpectedReturn(new BigDecimal("25.00"));
        r3.setRiskLevel(RiskLevel.HIGH);

        var r4 = new Recommendation();
        r4.setUser(user);
        r4.setInvestorProfile(InvestorProfile.AGGRESSIVE);
        r4.setTitle("Ethereum (ETH)");
        r4.setDescription("Segunda maior criptomoeda por capitalização. Base para DeFi e smart contracts. Alta volatilidade com potencial de valorização expressiva.");
        r4.setAssetType(AssetType.CRYPTO);
        r4.setExpectedReturn(new BigDecimal("35.00"));
        r4.setRiskLevel(RiskLevel.HIGH);

        return List.of(r1, r2, r3, r4);
    }
}
