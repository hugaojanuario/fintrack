package br.com.fintrack.domain.debt.service;

import br.com.fintrack.domain.debt.entity.Debt;
import br.com.fintrack.domain.debt.entity.dtos.CreateDebtRequestDTO;
import br.com.fintrack.domain.debt.entity.dtos.DebtResponseDTO;
import br.com.fintrack.domain.debt.entity.dtos.RegisterPaymentRequestDTO;
import br.com.fintrack.domain.debt.entity.dtos.UpdateDebtRequestDTO;
import br.com.fintrack.domain.debt.entity.enums.DebtStatus;
import br.com.fintrack.domain.debt.repository.DebtRepository;
import br.com.fintrack.domain.user.entity.User;
import br.com.fintrack.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DebtService {

    private final DebtRepository repository;
    private final UserRepository userRepository;

    public DebtResponseDTO create(CreateDebtRequestDTO request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        Debt debt = new Debt();
        debt.setUser(user);
        debt.setCreditor(request.creditor());
        debt.setDescription(request.description());
        debt.setTotalAmount(request.totalAmount());
        debt.setRemainingAmount(request.totalAmount());
        debt.setInterestRate(request.interestRate());
        debt.setDueDay(request.dueDay());
        debt.setStatus(DebtStatus.ACTIVE);
        debt.setActive(true);

        Debt saved = repository.save(debt);

        return new DebtResponseDTO(saved);
    }

    public Page<DebtResponseDTO> getAll(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        return repository.findByUserAndActiveTrue(user, pageable).map(DebtResponseDTO::new);
    }

    public DebtResponseDTO getById(UUID id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        Debt debt = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException());

        return new DebtResponseDTO(debt);
    }

    public DebtResponseDTO update(UUID id, UpdateDebtRequestDTO request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        Debt debt = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException());

        if (request.creditor() != null) debt.setCreditor(request.creditor());
        if (request.description() != null) debt.setDescription(request.description());
        if (request.interestRate() != null) debt.setInterestRate(request.interestRate());
        if (request.dueDay() != null) debt.setDueDay(request.dueDay());

        Debt updated = repository.save(debt);

        return new DebtResponseDTO(updated);
    }

    public void softDelete(UUID id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        Debt debt = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException());

        debt.setActive(false);
        repository.save(debt);
    }

    public DebtResponseDTO registerPayment(UUID id, RegisterPaymentRequestDTO request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        Debt debt = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException());

        BigDecimal newRemaining = debt.getRemainingAmount()
                .subtract(request.amount())
                .setScale(2, RoundingMode.HALF_UP);

        if (newRemaining.compareTo(BigDecimal.ZERO) <= 0) {
            debt.setRemainingAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            debt.setStatus(DebtStatus.PAID);
        } else {
            debt.setRemainingAmount(newRemaining);
        }

        Debt updated = repository.save(debt);

        return new DebtResponseDTO(updated);
    }

    public BigDecimal calculateMonthlyInterest(UUID id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        Debt debt = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException());

        return debt.getRemainingAmount()
                .multiply(debt.getInterestRate().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
    }

}
