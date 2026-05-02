package br.com.fintrack.domain.webhook.controller;

import br.com.fintrack.domain.webhook.entity.dtos.CreateWebhookConfigRequestDTO;
import br.com.fintrack.domain.webhook.entity.dtos.UpdateWebhookConfigRequestDTO;
import br.com.fintrack.domain.webhook.entity.dtos.WebhookConfigResponseDTO;
import br.com.fintrack.domain.webhook.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Webhooks", description = "Configuração de webhooks com assinatura HMAC-SHA256")
@SecurityRequirement(name = "bearerAuth")
public class WebhookController {

    private final WebhookService service;

    @PostMapping
    @Operation(summary = "Registra uma nova configuração de webhook")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Webhook criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<WebhookConfigResponseDTO> create(@RequestBody @Valid CreateWebhookConfigRequestDTO request,
                                                            Authentication authentication,
                                                            UriComponentsBuilder uriBuilder) {
        var newWebhook = service.create(request, authentication.getName());
        var uri = uriBuilder.path("/api/v1/webhooks/{id}").buildAndExpand(newWebhook.id()).toUri();

        return ResponseEntity.created(uri).body(newWebhook);
    }

    @GetMapping
    @Operation(summary = "Lista os webhooks ativos do usuário (paginado)")
    @ApiResponse(responseCode = "200", description = "Lista de webhooks")
    public ResponseEntity<Page<WebhookConfigResponseDTO>> getAll(@PageableDefault(size = 10) Pageable pageable,
                                                                  Authentication authentication) {
        var webhooks = service.getAll(authentication.getName(), pageable);

        return ResponseEntity.ok(webhooks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retorna um webhook pelo ID")
    @ApiResponse(responseCode = "200", description = "Webhook encontrado")
    public ResponseEntity<WebhookConfigResponseDTO> getById(@Parameter(description = "ID do webhook") @PathVariable UUID id,
                                                             Authentication authentication) {
        var webhook = service.getById(id, authentication.getName());

        return ResponseEntity.ok(webhook);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um webhook")
    @ApiResponse(responseCode = "200", description = "Webhook atualizado")
    public ResponseEntity<WebhookConfigResponseDTO> update(@Parameter(description = "ID do webhook") @PathVariable UUID id,
                                                            @RequestBody @Valid UpdateWebhookConfigRequestDTO request,
                                                            Authentication authentication) {
        var updated = service.update(id, request, authentication.getName());

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativa um webhook (soft delete)")
    @ApiResponse(responseCode = "204", description = "Webhook desativado")
    public ResponseEntity<Void> delete(@Parameter(description = "ID do webhook") @PathVariable UUID id,
                                        Authentication authentication) {
        service.softDelete(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/receive")
    @Operation(summary = "Recebe um evento externo e valida a assinatura HMAC-SHA256")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento recebido e validado"),
            @ApiResponse(responseCode = "400", description = "Assinatura inválida")
    })
    public ResponseEntity<Void> receive(@Parameter(description = "ID do webhook") @PathVariable UUID id,
                                         @RequestBody String rawBody,
                                         @RequestHeader("X-FinTrack-Signature") String signature) {
        service.verifyAndReceive(id, rawBody, signature);

        return ResponseEntity.ok().build();
    }
}
