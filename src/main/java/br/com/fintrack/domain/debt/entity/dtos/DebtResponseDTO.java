package br.com.fintrack.domain.debt.entity.dtos;

import br.com.fintrack.domain.debt.entity.Debt;
import br.com.fintrack.domain.debt.entity.enums.DebtStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DebtResponseDTO(
        UUID id,
        String creditor,
        String description,
        BigDecimal totalAmount,
        BigDecimal remainingAmount,
        BigDecimal interestRate,
        Integer dueDay,
        DebtStatus status,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public DebtResponseDTO(Debt debt) {
        this(
                debt.getId(),
                debt.getCreditor(),
                debt.getDescription(),
                debt.getTotalAmount(),
                debt.getRemainingAmount(),
                debt.getInterestRate(),
                debt.getDueDay(),
                debt.getStatus(),
                debt.isActive(),
                debt.getCreatedAt(),
                debt.getUpdatedAt()
        );
    }

}
