package br.com.fintrack.domain.webhook.service;

import br.com.fintrack.domain.user.repository.UserRepository;
import br.com.fintrack.domain.webhook.entity.WebhookConfig;
import br.com.fintrack.domain.webhook.entity.dtos.CreateWebhookConfigRequestDTO;
import br.com.fintrack.domain.webhook.entity.dtos.UpdateWebhookConfigRequestDTO;
import br.com.fintrack.domain.webhook.entity.dtos.WebhookConfigResponseDTO;
import br.com.fintrack.domain.webhook.entity.enums.WebhookEventType;
import br.com.fintrack.domain.webhook.repository.WebhookConfigRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private final WebhookConfigRepository repository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final RestClient.Builder restClientBuilder;

    public WebhookConfigResponseDTO create(CreateWebhookConfigRequestDTO request, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        WebhookConfig webhook = new WebhookConfig();
        webhook.setUser(user);
        webhook.setUrl(request.url());
        webhook.setSecret(request.secret());
        webhook.setEventType(request.eventType());
        webhook.setFailureCount(0);
        webhook.setActive(true);

        WebhookConfig saved = repository.save(webhook);

        return new WebhookConfigResponseDTO(saved);
    }

    public Page<WebhookConfigResponseDTO> getAll(String userEmail, Pageable pageable) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        return repository.findByUserAndActiveTrue(user, pageable).map(WebhookConfigResponseDTO::new);
    }

    public WebhookConfigResponseDTO getById(UUID id, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        WebhookConfig webhook = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException());

        return new WebhookConfigResponseDTO(webhook);
    }

    public WebhookConfigResponseDTO update(UUID id, UpdateWebhookConfigRequestDTO request, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        WebhookConfig webhook = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException());

        if (request.url() != null) webhook.setUrl(request.url());
        if (request.secret() != null) webhook.setSecret(request.secret());
        if (request.eventType() != null) webhook.setEventType(request.eventType());

        WebhookConfig updated = repository.save(webhook);

        return new WebhookConfigResponseDTO(updated);
    }

    public void softDelete(UUID id, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        WebhookConfig webhook = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException());

        webhook.setActive(false);
        repository.save(webhook);
    }

    public void dispatch(WebhookEventType eventType, Object payload) {
        var webhooks = repository.findByEventTypeAndActiveTrue(eventType);

        String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        for (WebhookConfig webhook : webhooks) {
            var signature = computeHmac(json, webhook.getSecret());
            dispatchWithRetry(webhook, json, signature);
        }
    }

    public void verifyAndReceive(UUID webhookId, String rawBody, String signature) {
        WebhookConfig webhook = repository.findById(webhookId)
                .orElseThrow(() -> new RuntimeException());

        var expected = computeHmac(rawBody, webhook.getSecret());

        if (!expected.equals(signature)) {
            throw new RuntimeException();
        }
    }

    private void dispatchWithRetry(WebhookConfig webhook, String payload, String signature) {
        var client = restClientBuilder.build();
        int attempts = 0;

        while (attempts < 3) {
            try {
                client.post()
                        .uri(webhook.getUrl())
                        .header("X-FinTrack-Signature", signature)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(payload)
                        .retrieve()
                        .toBodilessEntity();
                return;
            } catch (Exception e) {
                attempts++;
            }
        }

        webhook.setFailureCount(webhook.getFailureCount() + 1);
        if (webhook.getFailureCount() >= 3) {
            webhook.setActive(false);
        }
        repository.save(webhook);
    }

    private String computeHmac(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            var keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return "sha256=" + HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
