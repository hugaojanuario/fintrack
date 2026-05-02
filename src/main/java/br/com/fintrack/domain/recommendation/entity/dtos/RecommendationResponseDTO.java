package br.com.fintrack.domain.recommendation.entity.dtos;

import br.com.fintrack.domain.recommendation.entity.Recommendation;
import br.com.fintrack.domain.recommendation.entity.enums.AssetType;
import br.com.fintrack.domain.recommendation.entity.enums.RiskLevel;
import br.com.fintrack.domain.user.entity.enums.InvestorProfile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record RecommendationResponseDTO(
        UUID id,
        InvestorProfile investorProfile,
        String title,
        String description,
        AssetType assetType,
        BigDecimal expectedReturn,
        RiskLevel riskLevel,
        LocalDateTime createdAt) {

    public RecommendationResponseDTO(Recommendation recommendation) {
        this(
                recommendation.getId(),
                recommendation.getInvestorProfile(),
                recommendation.getTitle(),
                recommendation.getDescription(),
                recommendation.getAssetType(),
                recommendation.getExpectedReturn(),
                recommendation.getRiskLevel(),
                recommendation.getCreatedAt()
        );
    }
}
