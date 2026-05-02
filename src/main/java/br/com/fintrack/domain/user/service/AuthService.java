package br.com.fintrack.domain.user.service;

import br.com.fintrack.config.TokenService;
import br.com.fintrack.domain.user.entity.User;
import br.com.fintrack.domain.user.entity.dtos.CreateUserRequestDTO;
import br.com.fintrack.domain.user.entity.dtos.auth.AuthDTO;
import br.com.fintrack.domain.user.entity.dtos.auth.AuthRegisterResponseDTO;
import br.com.fintrack.domain.user.entity.dtos.auth.AuthResponseDTO;
import br.com.fintrack.domain.user.entity.enums.UserRole;
import br.com.fintrack.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException());
    }

    public AuthResponseDTO login(AuthDTO request) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        var auth = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generatedToken((User) auth.getPrincipal());

        return new AuthResponseDTO(token);
    }

    public AuthRegisterResponseDTO register(CreateUserRequestDTO request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);
        user.setInvestorProfile(request.investorProfile());
        user.setMonthlyIncome(request.monthlyIncome());
        user.setActive(true);

        User saved = userRepository.save(user);

        return new AuthRegisterResponseDTO(saved);
    }

}
