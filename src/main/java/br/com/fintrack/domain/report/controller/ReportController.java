package br.com.fintrack.domain.report.controller;

import br.com.fintrack.domain.report.service.ReportGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports", description = "Geração de relatórios PDF enviados ao e-mail via AWS SES")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportGeneratorService service;

    @PostMapping("/generate")
    @Operation(summary = "Dispara a geração assíncrona do relatório financeiro em PDF")
    @ApiResponse(responseCode = "202", description = "Relatório sendo gerado — será enviado por e-mail")
    public ResponseEntity<Void> generate(Authentication authentication) {
        service.generateAndSend(authentication.getName());

        return ResponseEntity.accepted().build();
    }
}
