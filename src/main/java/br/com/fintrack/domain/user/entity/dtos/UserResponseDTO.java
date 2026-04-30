package br.com.fintrack.domain.user.entity.dtos;

import br.com.fintrack.domain.user.entity.User;
import br.com.fintrack.domain.user.entity.enums.InvestorProfile;
import br.com.fintrack.domain.user.entity.enums.UserRole;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        String passwordHash,
        UserRole role,
        InvestorProfile investorProfile,
        BigDecimal monthlyIncome,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public UserResponseDTO (User user){
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole(),
                user.getInvestorProfile(),
                user.getMonthlyIncome(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

}
