package br.com.fintrack.domain.expense.controller;

import br.com.fintrack.domain.expense.entity.dtos.CreateExpenseRequestDTO;
import br.com.fintrack.domain.expense.entity.dtos.ExpenseResponseDTO;
import br.com.fintrack.domain.expense.entity.dtos.UpdateExpenseRequestDTO;
import br.com.fintrack.domain.expense.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService service;

    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> create(@RequestBody @Valid CreateExpenseRequestDTO request, Authentication authentication, UriComponentsBuilder uriBuilder) {
        var newExpense = service.create(request, authentication.getName());
        var uri = uriBuilder.path("/api/v1/expenses/{id}").buildAndExpand(newExpense.id()).toUri();

        return ResponseEntity.created(uri).body(newExpense);
    }

    @GetMapping
    public ResponseEntity<Page<ExpenseResponseDTO>> getAll(@PageableDefault(size = 5) Pageable pageable,
                                                            Authentication authentication) {
        var expenses = service.getAll(authentication.getName(), pageable);

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> getById(@PathVariable UUID id,
                                                       Authentication authentication) {
        var expense = service.getById(id, authentication.getName());

        return ResponseEntity.ok(expense);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> update(@PathVariable UUID id,
                                                      @RequestBody @Valid UpdateExpenseRequestDTO request,
                                                      Authentication authentication) {
        var updated = service.update(id, request, authentication.getName());

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        service.softDelete(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }

}
