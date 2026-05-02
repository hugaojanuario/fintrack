package br.com.fintrack.domain.kafka;

import br.com.fintrack.domain.kafka.events.GoalAchievedEvent;
import br.com.fintrack.domain.kafka.events.MarketQuoteUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishGoalAchievedEvent(GoalAchievedEvent event) {
        kafkaTemplate.send("fintrack.goal.achieved", event);
    }

    public void publishMarketQuoteUpdatedEvent(MarketQuoteUpdatedEvent event) {
        kafkaTemplate.send("fintrack.market.quote.updated", event);
    }
}
