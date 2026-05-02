package br.com.fintrack.domain.goal.controller;

import br.com.fintrack.domain.goal.entity.dtos.ContributeGoalRequestDTO;
import br.com.fintrack.domain.goal.entity.dtos.CreateGoalRequestDTO;
import br.com.fintrack.domain.goal.entity.dtos.GoalResponseDTO;
import br.com.fintrack.domain.goal.entity.dtos.UpdateGoalRequestDTO;
import br.com.fintrack.domain.goal.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goals")
public class GoalController {

    private final GoalService service;

    @PostMapping
    public ResponseEntity<GoalResponseDTO> create(@RequestBody @Valid CreateGoalRequestDTO request,
                                                   Authentication authentication,
                                                   UriComponentsBuilder uriBuilder) {
        var newGoal = service.create(request, authentication.getName());
        var uri = uriBuilder.path("/api/v1/goals/{id}").buildAndExpand(newGoal.id()).toUri();

        return ResponseEntity.created(uri).body(newGoal);
    }

    @GetMapping
    public ResponseEntity<Page<GoalResponseDTO>> getAll(@PageableDefault(size = 5) Pageable pageable,
                                                         Authentication authentication) {
        var goals = service.getAll(authentication.getName(), pageable);

        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponseDTO> getById(@PathVariable UUID id,
                                                    Authentication authentication) {
        var goal = service.getById(id, authentication.getName());

        return ResponseEntity.ok(goal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponseDTO> update(@PathVariable UUID id,
                                                   @RequestBody @Valid UpdateGoalRequestDTO request,
                                                   Authentication authentication) {
        var updated = service.update(id, request, authentication.getName());

        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/contribute")
    public ResponseEntity<GoalResponseDTO> contribute(@PathVariable UUID id,
                                                       @RequestBody @Valid ContributeGoalRequestDTO request,
                                                       Authentication authentication) {
        var updated = service.contribute(id, request, authentication.getName());

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        service.softDelete(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }
}
