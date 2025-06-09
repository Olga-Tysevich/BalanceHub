package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.dto.common.AccountDTO;
import by.testtask.balancehub.exceptions.UnauthorizedException;
import by.testtask.balancehub.mappers.AccountMapper;
import by.testtask.balancehub.repos.AccountRepo;
import by.testtask.balancehub.services.AccountService;
import by.testtask.balancehub.services.UserService;
import by.testtask.balancehub.utils.PrincipalExtractor;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserService userService;
    private final AccountRepo accountRepo;
    private final AccountMapper accountMapper;
    @Value("${spring.application.interestRate:0.00}")
    private BigDecimal interestRate;
    @Value("${spring.application.maxAllowedInterestRate:0.00}")
    private BigDecimal maxAllowedInterestRate;

    @Override
    public Long createAccount(AccountDTO accountDTO) {
        User currentUser = PrincipalExtractor.getCurrentUser();

        if (Objects.isNull(currentUser)) throw new UnauthorizedException();

        Long currentUserId = currentUser.getId();

        if (!currentUserId.equals(accountDTO.getUserId()))
            throw new AccessDeniedException("The current user is not allowed to modify this account. Current user id: " + currentUserId
                    + " account owner id: " + accountDTO.getUserId());

        if (accountRepo.existsById(accountDTO.getId())) throw new RuntimeException("Account already exists");

        Account account = accountMapper.toEntity(accountDTO);
        account.setUser(currentUser);

        accountRepo.save(account);

        return account.getId();
    }

    @Override
    public void safelyIncreaseBalance() {
        int page = 0;
        int size = 500;
        Page<Long> accountIds;

        do {
            accountIds = accountRepo.findAccountIdsWithBalanceUpToPercent(maxAllowedInterestRate, PageRequest.of(page, size));
            accountIds.forEach(accountId -> {
                try {
                    processSingleAccount(accountId);
                } catch (Exception e) {
                    log.error("Error processing account {}: {}", accountId, e.getMessage());
                }
            });
            page++;
        } while (accountIds.hasNext());
    }

    @Timed(value = "account.balance.increase", description = "Time taken to increase balance")
    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class},
            backoff = @Backoff(delay = 100)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSingleAccount(Long accountId) {
        accountRepo.findByIdForUpdate(accountId).ifPresent(account -> {
            BigDecimal accountBonusBalance = account.getAvailableBonusBalance().add(account.getBonusHold());

            BigDecimal bonusBalance = accountBonusBalance.compareTo(BigDecimal.ZERO) == 0 ?
                    account.getInitialBalance() : accountBonusBalance;

            BigDecimal initialBalance = Optional.ofNullable(account.getInitialBalance())
                    .orElseThrow();
            BigDecimal maxAllowed = initialBalance.multiply(maxAllowedInterestRate);
            if (bonusBalance.compareTo(maxAllowed) >= 0) return;

            BigDecimal newBalance = bonusBalance.multiply(BigDecimal.ONE.add(interestRate));
            account.setBonusBalance(newBalance.min(maxAllowed));
            accountRepo.saveAndFlush(account);

            userService.evictCacheAndPublishEvent(account);
        });
    }

}