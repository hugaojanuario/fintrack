package br.com.fintrack.domain.webhook.repository;

import br.com.fintrack.domain.user.entity.User;
import br.com.fintrack.domain.webhook.entity.WebhookConfig;
import br.com.fintrack.domain.webhook.entity.enums.WebhookEventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebhookConfigRepository extends JpaRepository<WebhookConfig, UUID> {

    Page<WebhookConfig> findByUserAndActiveTrue(User user, Pageable pageable);

    Optional<WebhookConfig> findByIdAndUser(UUID id, User user);

    List<WebhookConfig> findByEventTypeAndActiveTrue(WebhookEventType eventType);
}
