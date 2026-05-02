package br.com.fintrack.domain.user.controller;

import br.com.fintrack.domain.user.entity.dtos.UpdateUserRequestDTO;
import br.com.fintrack.domain.user.entity.dtos.UserResponseDTO;
import br.com.fintrack.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMe(Authentication authentication) {
        var user = service.getMe(authentication.getName());

        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> update(@RequestBody @Valid UpdateUserRequestDTO request,
                                                   Authentication authentication) {
        var updated = service.update(authentication.getName(), request);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> delete(Authentication authentication) {
        service.softDelete(authentication.getName());

        return ResponseEntity.noContent().build();
    }

}
