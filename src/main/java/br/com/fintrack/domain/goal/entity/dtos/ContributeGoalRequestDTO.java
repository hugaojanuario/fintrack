package br.com.fintrack.domain.goal.entity.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ContributeGoalRequestDTO(
        @NotNull @Positive BigDecimal amount) {
}
