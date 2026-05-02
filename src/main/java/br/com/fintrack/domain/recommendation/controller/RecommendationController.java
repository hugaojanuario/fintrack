package br.com.fintrack.domain.recommendation.controller;

import br.com.fintrack.domain.recommendation.entity.dtos.RecommendationResponseDTO;
import br.com.fintrack.domain.recommendation.service.RecommendationService;
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
public class RecommendationController {

    private final RecommendationService service;

    @GetMapping
    public ResponseEntity<Page<RecommendationResponseDTO>> getAll(@PageableDefault(size = 10) Pageable pageable,
                                                                   Authentication authentication) {
        var recommendations = service.getAll(authentication.getName(), pageable);

        return ResponseEntity.ok(recommendations);
    }

    @PostMapping("/generate")
    public ResponseEntity<List<RecommendationResponseDTO>> generate(Authentication authentication) {
        var recommendations = service.generate(authentication.getName());

        return ResponseEntity.ok(recommendations);
    }
}
