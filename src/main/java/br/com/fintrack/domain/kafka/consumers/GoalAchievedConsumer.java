package br.com.fintrack.domain.kafka.consumers;

import br.com.fintrack.domain.kafka.events.GoalAchievedEvent;
import br.com.fintrack.domain.webhook.entity.enums.WebhookEventType;
import br.com.fintrack.domain.webhook.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoalAchievedConsumer {

    private final WebhookService webhookService;

    @KafkaListener(topics = "fintrack.goal.achieved", groupId = "fintrack-group")
    public void consume(GoalAchievedEvent event) {
        webhookService.dispatch(WebhookEventType.GOAL_ACHIEVED, event);
    }
}
