package br.com.fintrack.domain.debt.controller;

import br.com.fintrack.domain.debt.entity.dtos.CreateDebtRequestDTO;
import br.com.fintrack.domain.debt.entity.dtos.DebtResponseDTO;
import br.com.fintrack.domain.debt.entity.dtos.RegisterPaymentRequestDTO;
import br.com.fintrack.domain.debt.entity.dtos.UpdateDebtRequestDTO;
import br.com.fintrack.domain.debt.service.DebtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/debts")
@RequiredArgsConstructor
public class DebtController {

    private final DebtService service;

    @PostMapping
    public ResponseEntity<DebtResponseDTO> create(@RequestBody @Valid CreateDebtRequestDTO request,
                                                   Authentication authentication,
                                                   UriComponentsBuilder uriBuilder) {
        var newDebt = service.create(request, authentication.getName());
        var uri = uriBuilder.path("/api/v1/debts/{id}").buildAndExpand(newDebt.id()).toUri();

        return ResponseEntity.created(uri).body(newDebt);
    }

    @GetMapping
    public ResponseEntity<Page<DebtResponseDTO>> getAll(@PageableDefault(size = 5) Pageable pageable,
                                                         Authentication authentication) {
        var debts = service.getAll(authentication.getName(), pageable);

        return ResponseEntity.ok(debts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DebtResponseDTO> getById(@PathVariable UUID id,
                                                    Authentication authentication) {
        var debt = service.getById(id, authentication.getName());

        return ResponseEntity.ok(debt);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DebtResponseDTO> update(@PathVariable UUID id,
                                                   @RequestBody @Valid UpdateDebtRequestDTO request,
                                                   Authentication authentication) {
        var updated = service.update(id, request, authentication.getName());

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        service.softDelete(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/payment")
    public ResponseEntity<DebtResponseDTO> registerPayment(@PathVariable UUID id,
                                                            @RequestBody @Valid RegisterPaymentRequestDTO request,
                                                            Authentication authentication) {
        var updated = service.registerPayment(id, request, authentication.getName());

        return ResponseEntity.ok(updated);
    }

}
