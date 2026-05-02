package br.com.fintrack.domain.user.entity.dtos.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthDTO(
        @NotBlank String email,
        @NotBlank String password
) {
}
