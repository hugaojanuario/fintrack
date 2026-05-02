package br.com.fintrack.domain.budget.controller;

import br.com.fintrack.domain.budget.entity.MonthlyBudget;
import br.com.fintrack.domain.budget.entity.dtos.BudgetSummaryDTO;
import br.com.fintrack.domain.budget.entity.dtos.DebtProjectionDTO;
import br.com.fintrack.domain.budget.entity.dtos.GoalProjectionDTO;
import br.com.fintrack.domain.budget.entity.enums.DebtPaymentStrategy;
import br.com.fintrack.domain.budget.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/budget")
@Tag(name = "Budget", description = "Motor de orçamento com algoritmos snowball e avalanche")
@SecurityRequirement(name = "bearerAuth")
public class BudgetController {

    private final BudgetService service;

    @GetMapping("/summary")
    @Operation(summary = "Retorna o resumo financeiro mensal em tempo real")
    @ApiResponse(responseCode = "200", description = "Resumo do orçamento")
    public ResponseEntity<BudgetSummaryDTO> getSummary(Authentication authentication) {
        var summary = service.getSummary(authentication.getName());

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/history")
    @Operation(summary = "Lista os snapshots mensais do orçamento (paginado)")
    @ApiResponse(responseCode = "200", description = "Histórico de orçamentos")
    public ResponseEntity<Page<MonthlyBudget>> getHistory(@PageableDefault(size = 12) Pageable pageable,
                                                           Authentication authentication) {
        var history = service.getHistory(authentication.getName(), pageable);

        return ResponseEntity.ok(history);
    }

    @GetMapping("/debt-projection")
    @Operation(summary = "Projeta a quitação das dívidas pela estratégia escolhida")
    @ApiResponse(responseCode = "200", description = "Projeção de quitação das dívidas")
    public ResponseEntity<List<DebtProjectionDTO>> getDebtProjection(
            @Parameter(description = "Estratégia de pagamento: SNOWBALL (menor saldo primeiro) ou AVALANCHE (maior juros primeiro)")
            @RequestParam DebtPaymentStrategy strategy,
            Authentication authentication) {
        var projection = service.getDebtProjection(authentication.getName(), strategy);

        return ResponseEntity.ok(projection);
    }

    @GetMapping("/goals-projection")
    @Operation(summary = "Projeta o atingimento das metas com base no orçamento disponível")
    @ApiResponse(responseCode = "200", description = "Projeção das metas")
    public ResponseEntity<List<GoalProjectionDTO>> getGoalsProjection(Authentication authentication) {
        var projection = service.getGoalsProjection(authentication.getName());

        return ResponseEntity.ok(projection);
    }
}
