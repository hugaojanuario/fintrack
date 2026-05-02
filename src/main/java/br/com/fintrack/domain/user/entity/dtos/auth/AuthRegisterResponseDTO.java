package br.com.fintrack.domain.user.entity.dtos.auth;

import br.com.fintrack.domain.user.entity.User;
import br.com.fintrack.domain.user.entity.enums.InvestorProfile;
import br.com.fintrack.domain.user.entity.enums.UserRole;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuthRegisterResponseDTO(
        UUID id,
        String name,
        String email,
        UserRole role,
        InvestorProfile investorProfile,
        BigDecimal monthlyIncome,
        LocalDateTime createdAt
) {
    public AuthRegisterResponseDTO(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getInvestorProfile(),
                user.getMonthlyIncome(),
                user.getCreatedAt()
        );
    }

}
