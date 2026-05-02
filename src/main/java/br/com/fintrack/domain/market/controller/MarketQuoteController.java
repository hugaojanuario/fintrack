package br.com.fintrack.domain.market.controller;

import br.com.fintrack.domain.market.entity.dtos.MarketQuoteResponseDTO;
import br.com.fintrack.domain.market.service.MarketQuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/market")
@Tag(name = "Market", description = "Cotações de mercado via BRAPI (atualizado a cada 15 min)")
@SecurityRequirement(name = "bearerAuth")
public class MarketQuoteController {

    private final MarketQuoteService service;

    @GetMapping("/quotes")
    @Operation(summary = "Retorna as últimas cotações de todos os tickers monitorados")
    @ApiResponse(responseCode = "200", description = "Lista de cotações")
    public ResponseEntity<List<MarketQuoteResponseDTO>> getAll() {
        var quotes = service.getAll();

        return ResponseEntity.ok(quotes);
    }

    @GetMapping("/quotes/{ticker}")
    @Operation(summary = "Retorna a última cotação de um ticker específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cotação encontrada"),
            @ApiResponse(responseCode = "404", description = "Ticker não encontrado")
    })
    public ResponseEntity<MarketQuoteResponseDTO> getByTicker(
            @Parameter(description = "Código do ticker (ex: PETR4, VALE3)") @PathVariable String ticker) {
        var quote = service.getByTicker(ticker);

        return ResponseEntity.ok(quote);
    }
}
