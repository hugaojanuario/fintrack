package br.com.fintrack.domain.goal.entity.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateGoalRequestDTO(
        @NotBlank String title,
        @NotNull @Positive BigDecimal targetAmount,
        @NotNull @Future LocalDate deadline) {
}