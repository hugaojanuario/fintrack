package br.com.fintrack.domain.kafka.consumers;

import br.com.fintrack.domain.kafka.events.GoalAchievedEvent;
import br.com.fintrack.domain.report.service.EmailService;
import br.com.fintrack.domain.user.repository.UserRepository;
import br.com.fintrack.domain.webhook.entity.enums.WebhookEventType;
import br.com.fintrack.domain.webhook.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoalAchievedConsumer {

    private final WebhookService webhookService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @KafkaListener(topics = "fintrack.goal.achieved", groupId = "fintrack-group")
    public void consume(GoalAchievedEvent event) {
        webhookService.dispatch(WebhookEventType.GOAL_ACHIEVED, event);

        var user = userRepository.findById(event.getUserId());
        user.ifPresent(u -> emailService.sendGoalAchieved(u.getEmail(), event.getTitle(), event.getTargetAmount()));
    }
}
