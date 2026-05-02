package br.com.fintrack.domain.goal.service;

import br.com.fintrack.domain.goal.entity.Goal;
import br.com.fintrack.domain.goal.entity.dtos.ContributeGoalRequestDTO;
import br.com.fintrack.domain.goal.entity.dtos.CreateGoalRequestDTO;
import br.com.fintrack.domain.goal.entity.dtos.GoalResponseDTO;
import br.com.fintrack.domain.goal.entity.dtos.UpdateGoalRequestDTO;
import br.com.fintrack.domain.goal.entity.enums.GoalStatus;
import br.com.fintrack.domain.goal.repository.GoalRepository;
import br.com.fintrack.domain.kafka.KafkaProducerService;
import br.com.fintrack.domain.kafka.events.GoalAchievedEvent;
import br.com.fintrack.domain.user.entity.User;
import br.com.fintrack.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository repository;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    public GoalResponseDTO create(CreateGoalRequestDTO request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        Goal goal = new Goal();
        goal.setUser(user);
        goal.setTitle(request.title());
        goal.setTargetAmount(request.targetAmount().setScale(2, RoundingMode.HALF_UP));
        goal.setCurrentAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        goal.setDeadline(request.deadline());
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setActive(true);

        Goal saved = repository.save(goal);

        return buildResponse(saved);
    }

    public Page<GoalResponseDTO> getAll(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        return repository.findByUserAndActiveTrue(user, pageable).map(this::buildResponse);
    }

    public GoalResponseDTO getById(UUID id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        Goal goal = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException());

        return buildResponse(goal);
    }

    public GoalResponseDTO update(UUID id, UpdateGoalRequestDTO request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        Goal goal = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException());

        if (request.title() != null) goal.setTitle(request.title());
        if (request.targetAmount() != null) goal.setTargetAmount(request.targetAmount().setScale(2, RoundingMode.HALF_UP));
        if (request.deadline() != null) goal.setDeadline(request.deadline());

        Goal updated = repository.save(goal);

        return buildResponse(updated);
    }

    public GoalResponseDTO contribute(UUID id, ContributeGoalRequestDTO request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        Goal goal = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException());

        var newAmount = goal.getCurrentAmount()
                .add(request.amount())
                .setScale(2, RoundingMode.HALF_UP);

        goal.setCurrentAmount(newAmount);

        if (newAmount.compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
        }

        Goal updated = repository.save(goal);

        if (updated.getStatus() == GoalStatus.COMPLETED) {
            kafkaProducerService.publishGoalAchievedEvent(new GoalAchievedEvent(
                    updated.getUser().getId(),
                    updated.getId(),
                    updated.getTitle(),
                    updated.getTargetAmount(),
                    LocalDateTime.now()
            ));
        }

        return buildResponse(updated);
    }

    public void softDelete(UUID id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        Goal goal = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException());

        goal.setActive(false);
        goal.setStatus(GoalStatus.CANCELLED);
        repository.save(goal);
    }

    private BigDecimal calculateMonthlyContribution(Goal goal) {
        var remaining = goal.getTargetAmount()
                .subtract(goal.getCurrentAmount())
                .setScale(2, RoundingMode.HALF_UP);

        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        var monthsRemaining = ChronoUnit.MONTHS.between(LocalDate.now(), goal.getDeadline());

        if (monthsRemaining <= 0) {
            return remaining;
        }

        return remaining.divide(BigDecimal.valueOf(monthsRemaining), 2, RoundingMode.HALF_UP);
    }

    private boolean isOnTrack(Goal goal) {
        var totalMonths = ChronoUnit.MONTHS.between(
                goal.getCreatedAt().toLocalDate(),
                goal.getDeadline()
        );

        if (totalMonths <= 0) {
            return goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0;
        }

        var elapsedMonths = ChronoUnit.MONTHS.between(
                goal.getCreatedAt().toLocalDate(),
                LocalDate.now()
        );

        var expectedProgress = BigDecimal.valueOf(elapsedMonths)
                .divide(BigDecimal.valueOf(totalMonths), 4, RoundingMode.HALF_UP);

        var actualProgress = goal.getCurrentAmount()
                .divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP);

        return actualProgress.compareTo(expectedProgress) >= 0;
    }

    private BigDecimal calculateAdditionalMonthlyNeeded(Goal goal, boolean onTrack) {
        if (onTrack) {
            return null;
        }

        var totalMonths = ChronoUnit.MONTHS.between(
                goal.getCreatedAt().toLocalDate(),
                goal.getDeadline()
        );

        if (totalMonths <= 0) {
            return null;
        }

        var originalMonthly = goal.getTargetAmount()
                .divide(BigDecimal.valueOf(totalMonths), 2, RoundingMode.HALF_UP);

        var currentMonthly = calculateMonthlyContribution(goal);

        var extra = currentMonthly.subtract(originalMonthly).setScale(2, RoundingMode.HALF_UP);

        return extra.compareTo(BigDecimal.ZERO) > 0 ? extra : null;
    }

    private GoalResponseDTO buildResponse(Goal goal) {
        var monthly = calculateMonthlyContribution(goal);
        var onTrack = isOnTrack(goal);
        var extra = calculateAdditionalMonthlyNeeded(goal, onTrack);

        return new GoalResponseDTO(goal, monthly, onTrack, extra);
    }
}
