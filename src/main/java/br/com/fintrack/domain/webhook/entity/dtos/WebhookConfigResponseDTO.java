package br.com.fintrack.domain.webhook.entity.dtos;

import br.com.fintrack.domain.webhook.entity.WebhookConfig;
import br.com.fintrack.domain.webhook.entity.enums.WebhookEventType;

import java.time.LocalDateTime;
import java.util.UUID;

public record WebhookConfigResponseDTO(
        UUID id,
        String url,
        WebhookEventType eventType,
        Integer failureCount,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public WebhookConfigResponseDTO(WebhookConfig webhook) {
        this(
                webhook.getId(),
                webhook.getUrl(),
                webhook.getEventType(),
                webhook.getFailureCount(),
                webhook.isActive(),
                webhook.getCreatedAt(),
                webhook.getUpdatedAt()
        );
    }
}
