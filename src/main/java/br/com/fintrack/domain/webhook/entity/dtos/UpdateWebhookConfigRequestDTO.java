package br.com.fintrack.domain.webhook.entity.dtos;

import br.com.fintrack.domain.webhook.entity.enums.WebhookEventType;
import org.hibernate.validator.constraints.URL;

public record UpdateWebhookConfigRequestDTO(
        @URL String url,
        String secret,
        WebhookEventType eventType) {
}
