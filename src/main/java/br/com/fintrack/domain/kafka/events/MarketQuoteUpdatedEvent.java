package br.com.fintrack.domain.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MarketQuoteUpdatedEvent {

    private String ticker;
    private BigDecimal price;
    private BigDecimal changePercent;
    private LocalDateTime updatedAt;
}
