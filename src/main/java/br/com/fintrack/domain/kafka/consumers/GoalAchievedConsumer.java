package br.com.fintrack.domain.kafka.consumers;

import br.com.fintrack.domain.kafka.events.GoalAchievedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoalAchievedConsumer {

    @KafkaListener(topics = "fintrack.goal.achieved", groupId = "fintrack-group")
    public void consume(GoalAchievedEvent event) {
    }
}
