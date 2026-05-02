package br.com.fintrack.domain.user.service;

import br.com.fintrack.domain.user.entity.User;
import br.com.fintrack.domain.user.entity.dtos.UpdateUserRequestDTO;
import br.com.fintrack.domain.user.entity.dtos.UserResponseDTO;
import br.com.fintrack.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO getMe(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        return new UserResponseDTO(user);
    }

    public UserResponseDTO update(String userEmail, UpdateUserRequestDTO request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        if (request.name() != null) user.setName(request.name());
        if (request.email() != null) user.setEmail(request.email());
        if (request.password() != null) user.setPasswordHash(passwordEncoder.encode(request.password()));
        if (request.investorProfile() != null) user.setInvestorProfile(request.investorProfile());
        if (request.monthlyIncome() != null) user.setMonthlyIncome(request.monthlyIncome());

        User updated = userRepository.save(user);

        return new UserResponseDTO(updated);
    }

    public void softDelete(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        user.setActive(false);
        userRepository.save(user);
    }

}
