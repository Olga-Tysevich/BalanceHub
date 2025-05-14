package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.domain.Transfer;
import by.testtask.balancehub.domain.TransferStatus;
import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.dto.common.AccountDTO;
import by.testtask.balancehub.dto.redis.TransferDTO;
import by.testtask.balancehub.dto.req.MoneyTransferReq;
import by.testtask.balancehub.events.Events;
import by.testtask.balancehub.exceptions.ProhibitedException;
import by.testtask.balancehub.exceptions.UnauthorizedException;
import by.testtask.balancehub.mappers.AccountMapper;
import by.testtask.balancehub.mappers.TransferMapper;
import by.testtask.balancehub.mappers.UserMapper;
import by.testtask.balancehub.repos.AccountRepo;
import by.testtask.balancehub.repos.TransferRepo;
import by.testtask.balancehub.services.AccountService;
import by.testtask.balancehub.utils.PrincipalExtractor;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
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
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final ApplicationEventPublisher eventPublisher;
    private final UserMapper userMapper;
    private final AccountRepo accountRepo;
    private final CacheManager cacheManager;
    private final AccountMapper accountMapper;
    private final TransferRepo transferRepo;
    private final TransferMapper transferMapper;
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
            accountIds.forEach(this::processSingleAccount);
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
        try {
            accountRepo.findByIdForUpdate(accountId).ifPresent(account -> {
                BigDecimal accountBonusBalance = account.getBonusBalance().multiply(account.getBonusHold());

                BigDecimal bonusBalance = accountBonusBalance.compareTo(BigDecimal.ZERO) == 0? account.getInitialBalance() : accountBonusBalance;
                BigDecimal initialBalance = Optional.ofNullable(account.getInitialBalance())
                        .orElseThrow();

                BigDecimal maxAllowed = initialBalance.multiply(maxAllowedInterestRate);
                if (bonusBalance.compareTo(maxAllowed) >= 0) return;

                BigDecimal newBalance = bonusBalance.add(bonusBalance.multiply(BigDecimal.ONE.add(interestRate)));
                account.setBonusBalance(newBalance.min(maxAllowed));
                accountRepo.saveAndFlush(account);

                evictCacheAndPublishEvent(account);
            });
        } catch (Exception e) {
            log.error("Error processing account {}: {}", accountId, e.getMessage());
        }
    }

    private void evictCacheAndPublishEvent(Account account) {
        User user = account.getUser();
        Optional.ofNullable(cacheManager.getCache("users"))
                .ifPresent(cache -> cache.evict(user.getId()));
        eventPublisher.publishEvent(new Events.UserChangedEvent(userMapper.toUserIndex(user)));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void makeTransfer(TransferDTO transferDTO) {
        log.info("Attempting to make a transfer from account id {} to account id {}", transferDTO.getFromAccountId(), transferDTO.getToAccountId());
        Account toAccount = accountRepo.findById(transferDTO.getToAccountId()).orElseThrow();

        BigDecimal transferAmount = transferDTO.getAmount();
        BigDecimal transferBonusAmount = transferDTO.getBonusAmount();
        BigDecimal newBalance = toAccount.getBalance().add(transferAmount).add(transferBonusAmount);

        try {
            toAccount.setBalance(newBalance);
            accountRepo.save(toAccount);

            Account fromAccount = accountRepo.findById(transferDTO.getFromAccountId()).orElseThrow();

            fromAccount.releaseFromHold(fromAccount.getHold().subtract(transferAmount));
            accountRepo.save(fromAccount);

            transferDTO.setStatus(TransferStatus.COMPLETED);
            transferDTO.setConfirmedAt(LocalDateTime.now());

            Transfer transfer = transferRepo.findById(transferDTO.getId()).orElseThrow();
            transfer.setFromAccount(fromAccount);
            transfer.setToAccount(toAccount);
            transfer.setStatus(TransferStatus.COMPLETED);
            transfer.setConfirmedAt(transferDTO.getConfirmedAt());

            transferRepo.save(transfer);

            Events.TransferConfirmed transferConfirmed = new Events.TransferConfirmed(transferDTO);

            transferDTO.setStatus(transfer.getStatus());
            transferDTO.setConfirmedAt(transfer.getConfirmedAt());
            eventPublisher.publishEvent(transferConfirmed);

            log.info("Transfer successfully confirmed for transfer id: {}", transferDTO.getId());

        } catch (Exception e) {
            log.error("Transfer failed for transfer id: {}. Reversing operations.", transferDTO.getId(), e);

            Transfer transfer = transferMapper.toEntity(transferDTO);
            transfer.setStatus(TransferStatus.FAILED);

            transferRepo.save(transfer);

            transferDTO.setStatus(TransferStatus.FAILED);

            Account fromAccount = accountRepo.findById(transferDTO.getFromAccountId()).orElseThrow();
            fromAccount.setBalance(fromAccount.getBalance().add(transferAmount));
            fromAccount.setBonusBalance(fromAccount.getBonusBalance().add(transferBonusAmount));

            fromAccount.releaseFromHold(fromAccount.getHold().subtract(transferAmount));
            fromAccount.releaseFromBonusHold(fromAccount.getBonusHold().subtract(transferBonusAmount));

            accountRepo.save(fromAccount);

            eventPublisher.publishEvent(transferDTO);
        }
    }

    @Override
    public Long createTransfer(MoneyTransferReq moneyTransferReq) {
        User currentUser = PrincipalExtractor.getCurrentUser();

        if (Objects.isNull(currentUser)) {
            log.error("Unauthorized access attempt. No current user found.");
            throw new UnauthorizedException();
        }

        Long fromAccountId = moneyTransferReq.getFromAccountId();
        Long currentUserId = currentUser.getId();

        log.info("Creating transfer request from account id: {} by user id: {}", fromAccountId, currentUserId);

        Optional<Long> accountOwnerId = accountRepo.findUserIdByAccountId(fromAccountId);

        if (accountOwnerId.isEmpty() || !accountOwnerId.get().equals(currentUserId)) {
            Long ownerId = moneyTransferReq.getFromAccountId();
            log.error("Prohibited action: Account owner id: {} does not match current user id: {}", accountOwnerId.orElse(null), currentUserId);

            throw new ProhibitedException("The account owner is different from the current user. " +
                    "Owner id: " + ownerId + ", current user id: " + currentUserId);
        }

        BigDecimal amount = moneyTransferReq.getAmount();

        Optional<Account> fromAccountOpt = accountRepo.findByIdAndSufficientBalance(fromAccountId, amount);

        if (fromAccountOpt.isEmpty()) {
            log.error("Insufficient balance for account id: {}. Transfer amount: {}", fromAccountId, amount);
            throw new ProhibitedException("Insufficient balance: the balance is too low for this operation. Account id: " + fromAccountId);
        }

        Long toAccountId = moneyTransferReq.getToAccountId();
        Optional<Account> toAccountOpt = accountRepo.findById(toAccountId);

        if (toAccountOpt.isEmpty()) {
            log.error("Recipient account does not exist. Account id: {}", toAccountId);
            throw new ProhibitedException("The specified recipient account does not exist!. Account id: " + toAccountOpt);
        }

        Account fromAccount = fromAccountOpt.get();
        Account toAccount = toAccountOpt.get();

        BigDecimal transferAmount = moneyTransferReq.getAmount();
        BigDecimal currentBalance = fromAccount.getBalance();
        BigDecimal bonusBalance = fromAccount.getBonusBalance();
        BigDecimal commonBalance = currentBalance.add(bonusBalance);

        BigDecimal writtenOffAmount = BigDecimal.ZERO;
        BigDecimal writtenOffBonusAmount = BigDecimal.ZERO;

        if (currentBalance.compareTo(transferAmount) >= 0) {

            fromAccount.addToHold(fromAccount.getHold().add(transferAmount));
            writtenOffAmount = transferAmount;

        } else if (bonusBalance.compareTo(transferAmount) >= 0) {

            fromAccount.addToBonusHold(fromAccount.getBonusHold().add(transferAmount));
            writtenOffBonusAmount = transferAmount;

        } else if (commonBalance.compareTo(transferAmount) >= 0) {

            BigDecimal remainingFromBonus = transferAmount.subtract(currentBalance);
            fromAccount.addToHold(fromAccount.getHold().add(currentBalance));
            fromAccount.addToBonusHold(fromAccount.getBonusHold().add(remainingFromBonus));
            writtenOffAmount = currentBalance;
            writtenOffBonusAmount = remainingFromBonus;

        }

        accountRepo.save(fromAccount);

        Transfer transfer = Transfer.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(writtenOffAmount)
                .bonusAmount(writtenOffBonusAmount)
                .status(TransferStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        transferRepo.save(transfer);

        TransferDTO transferDTO = transferMapper.toDTO(transfer);

        Events.TransferEvent transferEvent = new Events.TransferEvent(transferDTO);

        eventPublisher.publishEvent(transferEvent);

        log.info("Transfer request created successfully for transfer id: {}", transfer.getId());

        return transfer.getId();
    }
}