package br.com.fintrack.domain.kafka.consumers;

import br.com.fintrack.domain.kafka.events.MarketQuoteUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarketQuoteUpdatedConsumer {

    @KafkaListener(topics = "fintrack.market.quote.updated", groupId = "fintrack-group")
    public void consume(MarketQuoteUpdatedEvent event) {
    }
}
