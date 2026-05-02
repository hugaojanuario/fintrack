package br.com.fintrack.domain.user.entity.dtos;

import br.com.fintrack.domain.user.entity.enums.InvestorProfile;

import java.math.BigDecimal;

public record UpdateUserRequestDTO(
        String name,
        String email,
        String password,
        InvestorProfile investorProfile,
        BigDecimal monthlyIncome
) {
}
