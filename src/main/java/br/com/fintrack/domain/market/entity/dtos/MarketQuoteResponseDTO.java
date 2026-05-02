package br.com.fintrack.domain.market.entity.dtos;

import br.com.fintrack.domain.market.entity.MarketQuote;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MarketQuoteResponseDTO(
        UUID id,
        String ticker,
        String name,
        BigDecimal price,
        BigDecimal changePercent,
        BigDecimal marketCap,
        LocalDateTime quoteDate) {

    public MarketQuoteResponseDTO(MarketQuote quote) {
        this(
                quote.getId(),
                quote.getTicker(),
                quote.getName(),
                quote.getPrice(),
                quote.getChangePercent(),
                quote.getMarketCap(),
                quote.getQuoteDate()
        );
    }
}
