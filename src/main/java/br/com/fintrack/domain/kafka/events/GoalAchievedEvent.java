package br.com.fintrack.domain.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoalAchievedEvent {

    private UUID userId;
    private UUID goalId;
    private String title;
    private BigDecimal targetAmount;
    private LocalDateTime achievedAt;
}
