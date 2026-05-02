package br.com.fintrack.domain.goal.entity.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateGoalRequestDTO(
        String title,
        @Positive BigDecimal targetAmount,
        @Future LocalDate deadline) {
}