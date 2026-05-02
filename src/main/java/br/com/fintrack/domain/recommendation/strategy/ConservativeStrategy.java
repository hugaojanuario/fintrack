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
public class ConservativeStrategy implements InvestmentStrategy {

    @Override
    public List<Recommendation> generate(User user) {
        var r1 = new Recommendation();
        r1.setUser(user);
        r1.setInvestorProfile(InvestorProfile.CONSERVATIVE);
        r1.setTitle("Tesouro SELIC");
        r1.setDescription("Título público atrelado à taxa SELIC. Liquidez diária e baixíssimo risco. Ideal para reserva de emergência e objetivos de curto prazo.");
        r1.setAssetType(AssetType.TREASURY);
        r1.setExpectedReturn(new BigDecimal("10.50"));
        r1.setRiskLevel(RiskLevel.LOW);

        var r2 = new Recommendation();
        r2.setUser(user);
        r2.setInvestorProfile(InvestorProfile.CONSERVATIVE);
        r2.setTitle("CDB 110% CDI");
        r2.setDescription("Certificado de Depósito Bancário com rendimento acima do CDI. Coberto pelo FGC até R$ 250.000. Excelente para reservas de médio prazo.");
        r2.setAssetType(AssetType.FIXED_INCOME);
        r2.setExpectedReturn(new BigDecimal("11.55"));
        r2.setRiskLevel(RiskLevel.LOW);

        var r3 = new Recommendation();
        r3.setUser(user);
        r3.setInvestorProfile(InvestorProfile.CONSERVATIVE);
        r3.setTitle("Tesouro IPCA+");
        r3.setDescription("Título público com proteção contra a inflação mais taxa real. Garante poder de compra no longo prazo. Indicado para aposentadoria.");
        r3.setAssetType(AssetType.TREASURY);
        r3.setExpectedReturn(new BigDecimal("6.20"));
        r3.setRiskLevel(RiskLevel.LOW);

        return List.of(r1, r2, r3);
    }
}
