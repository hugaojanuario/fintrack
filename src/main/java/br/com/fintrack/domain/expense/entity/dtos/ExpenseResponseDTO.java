package br.com.fintrack.domain.expense.entity.dtos;

import br.com.fintrack.domain.expense.entity.Expense;
import br.com.fintrack.domain.expense.entity.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ExpenseResponseDTO(
        UUID id,
        String description,
        BigDecimal amount,
        ExpenseCategory category,
        Integer dueDay,
        boolean active,
        LocalDateTime createdAt
) {
    public ExpenseResponseDTO(Expense expense) {
        this(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDueDay(),
                expense.isActive(),
                expense.getCreatedAt()
        );
    }

}
