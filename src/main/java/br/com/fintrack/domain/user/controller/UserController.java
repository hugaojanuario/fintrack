package br.com.fintrack.domain.user.controller;

import br.com.fintrack.domain.user.entity.dtos.UpdateUserRequestDTO;
import br.com.fintrack.domain.user.entity.dtos.UserResponseDTO;
import br.com.fintrack.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Users", description = "Gerenciamento do perfil do usuário")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService service;

    @GetMapping("/me")
    @Operation(summary = "Retorna os dados do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Dados do usuário")
    public ResponseEntity<UserResponseDTO> getMe(Authentication authentication) {
        var user = service.getMe(authentication.getName());

        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    @Operation(summary = "Atualiza os dados do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<UserResponseDTO> update(@RequestBody @Valid UpdateUserRequestDTO request,
                                                   Authentication authentication) {
        var updated = service.update(authentication.getName(), request);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/me")
    @Operation(summary = "Desativa a conta do usuário autenticado")
    @ApiResponse(responseCode = "204", description = "Conta desativada")
    public ResponseEntity<Void> delete(Authentication authentication) {
        service.softDelete(authentication.getName());

        return ResponseEntity.noContent().build();
    }
}
