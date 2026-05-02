package br.com.fintrack.domain.debt.entity.dtos;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RegisterPaymentRequestDTO(@NotNull BigDecimal amount) {
}
