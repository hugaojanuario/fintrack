package br.com.fintrack.domain.report.controller;

import br.com.fintrack.domain.report.service.ReportGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportGeneratorService service;

    @PostMapping("/generate")
    public ResponseEntity<Void> generate(Authentication authentication) {
        service.generateAndSend(authentication.getName());

        return ResponseEntity.accepted().build();
    }
}
