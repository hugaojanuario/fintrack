package br.com.fintrack.domain.debt.controller;

import br.com.fintrack.domain.debt.entity.dtos.CreateDebtRequestDTO;
import br.com.fintrack.domain.debt.entity.dtos.DebtResponseDTO;
import br.com.fintrack.domain.debt.entity.dtos.RegisterPaymentRequestDTO;
import br.com.fintrack.domain.debt.entity.dtos.UpdateDebtRequestDTO;
import br.com.fintrack.domain.debt.service.DebtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Debts", description = "Gestão de dívidas e pagamentos")
@SecurityRequirement(name = "bearerAuth")
public class DebtController {

    private final DebtService service;

    @PostMapping
    @Operation(summary = "Registra uma nova dívida")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dívida criada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<DebtResponseDTO> create(@RequestBody @Valid CreateDebtRequestDTO request,
                                                   Authentication authentication,
                                                   UriComponentsBuilder uriBuilder) {
        var newDebt = service.create(request, authentication.getName());
        var uri = uriBuilder.path("/api/v1/debts/{id}").buildAndExpand(newDebt.id()).toUri();

        return ResponseEntity.created(uri).body(newDebt);
    }

    @GetMapping
    @Operation(summary = "Lista as dívidas ativas do usuário (paginado)")
    @ApiResponse(responseCode = "200", description = "Lista de dívidas")
    public ResponseEntity<Page<DebtResponseDTO>> getAll(@PageableDefault(size = 5) Pageable pageable,
                                                         Authentication authentication) {
        var debts = service.getAll(authentication.getName(), pageable);

        return ResponseEntity.ok(debts);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retorna uma dívida pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dívida encontrada"),
            @ApiResponse(responseCode = "404", description = "Dívida não encontrada")
    })
    public ResponseEntity<DebtResponseDTO> getById(@Parameter(description = "ID da dívida") @PathVariable UUID id,
                                                    Authentication authentication) {
        var debt = service.getById(id, authentication.getName());

        return ResponseEntity.ok(debt);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza os dados de uma dívida")
    @ApiResponse(responseCode = "200", description = "Dívida atualizada")
    public ResponseEntity<DebtResponseDTO> update(@Parameter(description = "ID da dívida") @PathVariable UUID id,
                                                   @RequestBody @Valid UpdateDebtRequestDTO request,
                                                   Authentication authentication) {
        var updated = service.update(id, request, authentication.getName());

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativa uma dívida (soft delete)")
    @ApiResponse(responseCode = "204", description = "Dívida desativada")
    public ResponseEntity<Void> delete(@Parameter(description = "ID da dívida") @PathVariable UUID id,
                                        Authentication authentication) {
        service.softDelete(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/payment")
    @Operation(summary = "Registra um pagamento e subtrai do saldo devedor")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagamento registrado"),
            @ApiResponse(responseCode = "404", description = "Dívida não encontrada")
    })
    public ResponseEntity<DebtResponseDTO> registerPayment(@Parameter(description = "ID da dívida") @PathVariable UUID id,
                                                            @RequestBody @Valid RegisterPaymentRequestDTO request,
                                                            Authentication authentication) {
        var updated = service.registerPayment(id, request, authentication.getName());

        return ResponseEntity.ok(updated);
    }
}
