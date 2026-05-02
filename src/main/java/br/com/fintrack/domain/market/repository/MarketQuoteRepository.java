package br.com.fintrack.domain.market.repository;

import br.com.fintrack.domain.market.entity.MarketQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MarketQuoteRepository extends JpaRepository<MarketQuote, UUID> {

    Optional<MarketQuote> findTopByTickerOrderByQuoteDateDesc(String ticker);

    @Query("SELECT m FROM MarketQuote m WHERE m.quoteDate = (SELECT MAX(m2.quoteDate) FROM MarketQuote m2 WHERE m2.ticker = m.ticker)")
    List<MarketQuote> findLatestPerTicker();
}
