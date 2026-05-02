package br.com.fintrack.domain.market.service;

import br.com.fintrack.domain.market.client.BrapiClient;
import br.com.fintrack.domain.market.client.BrapiQuoteDTO;
import br.com.fintrack.domain.market.entity.MarketQuote;
import br.com.fintrack.domain.market.entity.dtos.MarketQuoteResponseDTO;
import br.com.fintrack.domain.market.repository.MarketQuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketQuoteService {

    private final MarketQuoteRepository repository;
    private final BrapiClient brapiClient;

    @Value("${brapi.tickers}")
    private String tickers;

    public List<MarketQuoteResponseDTO> getAll() {
        return repository.findLatestPerTicker().stream()
                .map(MarketQuoteResponseDTO::new)
                .toList();
    }

    public MarketQuoteResponseDTO getByTicker(String ticker) {
        MarketQuote quote = repository.findTopByTickerOrderByQuoteDateDesc(ticker.toUpperCase())
                .orElseThrow(() -> new RuntimeException());

        return new MarketQuoteResponseDTO(quote);
    }

    @Scheduled(cron = "0 */15 * * * *")
    public void fetchAndSave() {
        var response = brapiClient.fetchQuotes(tickers);

        if (response == null || response.results() == null) return;

        for (BrapiQuoteDTO dto : response.results()) {
            var existing = repository.findTopByTickerOrderByQuoteDateDesc(dto.symbol());

            MarketQuote quote = existing.orElseGet(MarketQuote::new);
            quote.setTicker(dto.symbol());
            quote.setName(dto.longName());
            quote.setPrice(dto.regularMarketPrice().setScale(2, RoundingMode.HALF_UP));
            quote.setChangePercent(dto.regularMarketChangePercent() != null
                    ? dto.regularMarketChangePercent().setScale(4, RoundingMode.HALF_UP)
                    : null);
            quote.setMarketCap(dto.marketCap() != null
                    ? dto.marketCap().setScale(2, RoundingMode.HALF_UP)
                    : null);
            quote.setQuoteDate(LocalDateTime.now());

            repository.save(quote);
        }
    }
}
