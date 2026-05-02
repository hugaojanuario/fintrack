package br.com.fintrack.domain.market.controller;

import br.com.fintrack.domain.market.entity.dtos.MarketQuoteResponseDTO;
import br.com.fintrack.domain.market.service.MarketQuoteService;
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
public class MarketQuoteController {

    private final MarketQuoteService service;

    @GetMapping("/quotes")
    public ResponseEntity<List<MarketQuoteResponseDTO>> getAll() {
        var quotes = service.getAll();

        return ResponseEntity.ok(quotes);
    }

    @GetMapping("/quotes/{ticker}")
    public ResponseEntity<MarketQuoteResponseDTO> getByTicker(@PathVariable String ticker) {
        var quote = service.getByTicker(ticker);

        return ResponseEntity.ok(quote);
    }
}
