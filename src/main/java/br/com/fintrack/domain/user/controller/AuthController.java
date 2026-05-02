package br.com.fintrack.domain.user.controller;

import br.com.fintrack.domain.user.entity.dtos.CreateUserRequestDTO;
import br.com.fintrack.domain.user.entity.dtos.auth.AuthDTO;
import br.com.fintrack.domain.user.entity.dtos.auth.AuthRegisterResponseDTO;
import br.com.fintrack.domain.user.entity.dtos.auth.AuthResponseDTO;
import br.com.fintrack.domain.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticação e registro de usuários")
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    @Operation(summary = "Realiza login e retorna o token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Credenciais inválidas")
    })
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid AuthDTO request) {
        var response = service.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Registra um novo usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<AuthRegisterResponseDTO> register(@RequestBody @Valid CreateUserRequestDTO request,
                                                             UriComponentsBuilder uriBuilder) {
        var newUser = service.register(request);
        var uri = uriBuilder.path("/api/v1/users/me").build().toUri();

        return ResponseEntity.created(uri).body(newUser);
    }
}
