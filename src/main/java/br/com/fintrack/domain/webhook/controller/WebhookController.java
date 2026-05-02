package br.com.fintrack.domain.webhook.controller;

import br.com.fintrack.domain.webhook.entity.dtos.CreateWebhookConfigRequestDTO;
import br.com.fintrack.domain.webhook.entity.dtos.UpdateWebhookConfigRequestDTO;
import br.com.fintrack.domain.webhook.entity.dtos.WebhookConfigResponseDTO;
import br.com.fintrack.domain.webhook.service.WebhookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/webhooks")
public class WebhookController {

    private final WebhookService service;

    @PostMapping
    public ResponseEntity<WebhookConfigResponseDTO> create(@RequestBody @Valid CreateWebhookConfigRequestDTO request,
                                                            Authentication authentication,
                                                            UriComponentsBuilder uriBuilder) {
        var newWebhook = service.create(request, authentication.getName());
        var uri = uriBuilder.path("/api/v1/webhooks/{id}").buildAndExpand(newWebhook.id()).toUri();

        return ResponseEntity.created(uri).body(newWebhook);
    }

    @GetMapping
    public ResponseEntity<Page<WebhookConfigResponseDTO>> getAll(@PageableDefault(size = 10) Pageable pageable,
                                                                  Authentication authentication) {
        var webhooks = service.getAll(authentication.getName(), pageable);

        return ResponseEntity.ok(webhooks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebhookConfigResponseDTO> getById(@PathVariable UUID id,
                                                             Authentication authentication) {
        var webhook = service.getById(id, authentication.getName());

        return ResponseEntity.ok(webhook);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WebhookConfigResponseDTO> update(@PathVariable UUID id,
                                                            @RequestBody @Valid UpdateWebhookConfigRequestDTO request,
                                                            Authentication authentication) {
        var updated = service.update(id, request, authentication.getName());

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        service.softDelete(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/receive")
    public ResponseEntity<Void> receive(@PathVariable UUID id,
                                         @RequestBody String rawBody,
                                         @RequestHeader("X-FinTrack-Signature") String signature) {
        service.verifyAndReceive(id, rawBody, signature);

        return ResponseEntity.ok().build();
    }
}
