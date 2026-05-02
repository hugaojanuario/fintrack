package br.com.fintrack.domain.webhook.entity.dtos;

import br.com.fintrack.domain.webhook.entity.enums.WebhookEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

public record CreateWebhookConfigRequestDTO(
        @NotBlank @URL String url,
        @NotBlank String secret,
        @NotNull WebhookEventType eventType) {
}
