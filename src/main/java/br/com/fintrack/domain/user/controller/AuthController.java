package br.com.fintrack.domain.user.controller;

import br.com.fintrack.domain.user.entity.dtos.CreateUserRequestDTO;
import br.com.fintrack.domain.user.entity.dtos.auth.AuthDTO;
import br.com.fintrack.domain.user.entity.dtos.auth.AuthRegisterResponseDTO;
import br.com.fintrack.domain.user.entity.dtos.auth.AuthResponseDTO;
import br.com.fintrack.domain.user.service.AuthService;
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
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid AuthDTO request) {
        var response = service.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthRegisterResponseDTO> register(@RequestBody @Valid CreateUserRequestDTO request,
                                                             UriComponentsBuilder uriBuilder) {
        var newUser = service.register(request);
        var uri = uriBuilder.path("/api/v1/users/me").build().toUri();

        return ResponseEntity.created(uri).body(newUser);
    }

}
