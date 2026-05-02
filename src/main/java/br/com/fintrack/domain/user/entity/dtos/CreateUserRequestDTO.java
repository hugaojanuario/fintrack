package br.com.fintrack.domain.user.entity.dtos;

import br.com.fintrack.domain.user.entity.enums.InvestorProfile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateUserRequestDTO(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotNull InvestorProfile investorProfile,
        BigDecimal monthlyIncome
) {
}
