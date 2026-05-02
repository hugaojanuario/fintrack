package br.com.fintrack.domain.expense.service;

import br.com.fintrack.domain.expense.entity.Expense;
import br.com.fintrack.domain.expense.entity.dtos.CreateExpenseRequestDTO;
import br.com.fintrack.domain.expense.entity.dtos.ExpenseResponseDTO;
import br.com.fintrack.domain.expense.entity.dtos.UpdateExpenseRequestDTO;
import br.com.fintrack.domain.expense.repository.ExpenseRepository;
import br.com.fintrack.domain.user.entity.User;
import br.com.fintrack.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository repository;
    private final UserRepository userRepository;

    public ExpenseResponseDTO create(CreateExpenseRequestDTO request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setDescription(request.description());
        expense.setAmount(request.amount());
        expense.setCategory(request.category());
        expense.setDueDay(request.dueDay());
        expense.setActive(true);

        Expense saved = repository.save(expense);

        return new ExpenseResponseDTO(saved);
    }

    public Page<ExpenseResponseDTO> getAll(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        return repository.findByUserAndActiveTrue(user, pageable).map(ExpenseResponseDTO::new);
    }

    public ExpenseResponseDTO getById(UUID id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        Expense expense = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException());

        return new ExpenseResponseDTO(expense);
    }

    public ExpenseResponseDTO update(UUID id, UpdateExpenseRequestDTO request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        Expense expense = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException());

        if (request.description() != null) expense.setDescription(request.description());
        if (request.amount() != null) expense.setAmount(request.amount());
        if (request.category() != null) expense.setCategory(request.category());
        if (request.dueDay() != null) expense.setDueDay(request.dueDay());

        Expense updated = repository.save(expense);

        return new ExpenseResponseDTO(updated);
    }

    public void softDelete(UUID id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        Expense expense = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException());

        expense.setActive(false);
        repository.save(expense);
    }

}
