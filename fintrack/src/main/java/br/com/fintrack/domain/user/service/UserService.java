package br.com.fintrack.domain.user.service;

import br.com.fintrack.domain.user.entity.User;
import br.com.fintrack.domain.user.entity.dtos.CreateUserRequestDTO;
import br.com.fintrack.domain.user.entity.dtos.UpdateUserRequestDTO;
import br.com.fintrack.domain.user.entity.dtos.UserResponseDTO;
import br.com.fintrack.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public UserResponseDTO create (CreateUserRequestDTO request){
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(request.passwordHash());
        user.setRole(request.role());
        user.setInvestorProfile(request.investorProfile());
        user.setMonthlyIncome(request.monthlyIncome());
        user.setActive(true);

        User saved = repository.save(user);

        return new UserResponseDTO(saved);
    }

    public Page<UserResponseDTO> getAll (Pageable pageable){
        return repository.findByActiveTrue(pageable).map(UserResponseDTO:: new);
    }

    public UserResponseDTO getById (UUID id){
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException());

        return new UserResponseDTO(user);
    }

    public UserResponseDTO update (UUID id, UpdateUserRequestDTO request){
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException());

        if (request.email() != null) {
            user.setEmail(request.email());
        }
        if (request.passwordHash() != null){
            user.setPasswordHash(request.passwordHash());
        }
        if (request.role() != null){
            user.setRole(request.role());
        }
        if (request.investorProfile() != null){
            user.setInvestorProfile(request.investorProfile());
        }

        User upated = repository.save(user);

        return new UserResponseDTO(upated);
    }

    public void softDelete (UUID id){
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException());
        user.setActive(false);

        repository.save(user);
    }
}
