package br.com.fintrack.domain.user.controller;

import br.com.fintrack.domain.user.entity.dtos.CreateUserRequestDTO;
import br.com.fintrack.domain.user.entity.dtos.UpdateUserRequestDTO;
import br.com.fintrack.domain.user.entity.dtos.UserResponseDTO;
import br.com.fintrack.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/fintrack/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    public ResponseEntity<UserResponseDTO> create (@Valid @RequestBody CreateUserRequestDTO request, UriComponentsBuilder uriComponentsBuilder){
        var user = service.create(request);
        var uri = uriComponentsBuilder.path("/fintrack/users/{id}").buildAndExpand(user).toUri();

        return ResponseEntity.created(uri).body(user);
    }

    public ResponseEntity<Page<UserResponseDTO>> getAll (@PageableDefault(size = 5) Pageable pageable){
        var user = service.getAll(pageable);

        return ResponseEntity.ok().body(user);
    }

    public ResponseEntity<UserResponseDTO> getById (@PathVariable UUID id){
        var user = service.getById(id);

        return ResponseEntity.ok().body(user);
    }

    public ResponseEntity<UserResponseDTO> update (@PathVariable UUID id, @Valid @RequestBody UpdateUserRequestDTO request){
        var user = service.update(id, request);

        return ResponseEntity.ok().body(user);
    }

    public ResponseEntity<Void> softDelete (@PathVariable UUID id){
        return ResponseEntity.noContent().build();
    }
}
