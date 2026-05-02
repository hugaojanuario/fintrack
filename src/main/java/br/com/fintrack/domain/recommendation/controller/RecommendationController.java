package br.com.fintrack.domain.recommendation.controller;

import br.com.fintrack.domain.recommendation.entity.dtos.RecommendationResponseDTO;
import br.com.fintrack.domain.recommendation.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendations")
@Tag(name = "Recommendations", description = "Motor de recomendações de investimento por perfil (Strategy Pattern)")
@SecurityRequirement(name = "bearerAuth")
public class RecommendationController {

    private final RecommendationService service;

    @GetMapping
    @Operation(summary = "Lista as recomendações geradas para o usuário (paginado)")
    @ApiResponse(responseCode = "200", description = "Lista de recomendações")
    public ResponseEntity<Page<RecommendationResponseDTO>> getAll(@PageableDefault(size = 10) Pageable pageable,
                                                                   Authentication authentication) {
        var recommendations = service.getAll(authentication.getName(), pageable);

        return ResponseEntity.ok(recommendations);
    }

    @PostMapping("/generate")
    @Operation(summary = "Gera novas recomendações com base no perfil do investidor")
    @ApiResponse(responseCode = "200", description = "Recomendações geradas")
    public ResponseEntity<List<RecommendationResponseDTO>> generate(Authentication authentication) {
        var recommendations = service.generate(authentication.getName());

        return ResponseEntity.ok(recommendations);
    }
}
