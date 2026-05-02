package br.com.fintrack.domain.market.client;

import java.math.BigDecimal;

public record BrapiQuoteDTO(
        String symbol,
        String longName,
        BigDecimal regularMarketPrice,
        BigDecimal regularMarketChangePercent,
        BigDecimal marketCap) {
}
