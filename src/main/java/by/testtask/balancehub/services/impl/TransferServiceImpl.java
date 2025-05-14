package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.domain.Transfer;
import by.testtask.balancehub.domain.TransferStatus;
import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.dto.redis.TransferDTO;
import by.testtask.balancehub.dto.req.MoneyTransferReq;
import by.testtask.balancehub.events.Events;
import by.testtask.balancehub.exceptions.ProhibitedException;
import by.testtask.balancehub.exceptions.UnauthorizedException;
import by.testtask.balancehub.mappers.TransferMapper;
import by.testtask.balancehub.repos.AccountRepo;
import by.testtask.balancehub.repos.TransferRepo;
import by.testtask.balancehub.services.TransferService;
import by.testtask.balancehub.utils.PrincipalExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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
public class TransferServiceImpl implements TransferService {
    private final ApplicationEventPublisher eventPublisher;
    private final TransferRepo transferRepo;
    private final AccountRepo accountRepo;
    private final TransferMapper transferMapper;

    @Override
    public Long createTransfer(MoneyTransferReq moneyTransferReq) {
        User currentUser = PrincipalExtractor.getCurrentUser();

        if (Objects.isNull(currentUser)) {
            log.error("Unauthorized access attempt. No current user found.");
            throw new UnauthorizedException();
        }

        Long fromAccountId = moneyTransferReq.getFromAccountId();
        Long currentUserId = currentUser.getId();
        Optional<Long> accountOwnerId = accountRepo.findUserIdByAccountId(fromAccountId);

        if (accountOwnerId.isEmpty() || !accountOwnerId.get().equals(currentUserId)) {
            Long ownerId = moneyTransferReq.getFromAccountId();
            log.error("Prohibited action: Account owner id: {} does not match current user id: {}", accountOwnerId.orElse(null), currentUserId);

            throw new ProhibitedException("The account owner is different from the current user. " +
                    "Owner id: " + ownerId + ", current user id: " + currentUserId);
        }

        if (moneyTransferReq.getFromAccountId().equals(moneyTransferReq.getToAccountId())) {
            log.error("It is not possible to transfer to the same account! Account id: {}", fromAccountId);
            throw new ProhibitedException("It is not possible to transfer to the same account! Account id: " + fromAccountId);
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

        log.info("Creating transfer request from account id: {} by user id: {}", fromAccountId, currentUserId);

        Account fromAccount = fromAccountOpt.get();
        Account toAccount = toAccountOpt.get();

        BigDecimal transferAmount = moneyTransferReq.getAmount();
        BigDecimal currentBalance = fromAccount.getAvailableBalance();
        BigDecimal bonusBalance = fromAccount.getAvailableBonusBalance();
        BigDecimal commonBalance = currentBalance.add(bonusBalance);

        BigDecimal writtenOffAmount = BigDecimal.ZERO;
        BigDecimal writtenOffBonusAmount = BigDecimal.ZERO;

        if (currentBalance.compareTo(transferAmount) >= 0) {

            fromAccount.addToHold(transferAmount);
            writtenOffAmount = transferAmount;

        } else if (bonusBalance.compareTo(transferAmount) >= 0) {

            fromAccount.addToBonusHold(transferAmount);
            writtenOffBonusAmount = transferAmount;

        } else if (commonBalance.compareTo(transferAmount) >= 0) {

            BigDecimal remainingFromBonus = transferAmount.subtract(currentBalance);
            fromAccount.addToHold(currentBalance);
            fromAccount.addToBonusHold(remainingFromBonus);

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

    @Override
    public void makeTransfer(TransferDTO transferDTO) {
        log.info("Attempting to make a transfer from account id {} to account id {}", transferDTO.getFromAccountId(), transferDTO.getToAccountId());
        try {
            Transfer transfer = carryOutTransfer(transferDTO);

            Events.TransferConfirmed transferConfirmed = new Events.TransferConfirmed(transferDTO);

            transferDTO.setStatus(transfer.getStatus());
            transferDTO.setConfirmedAt(transfer.getConfirmedAt());

            log.info("Transfer successfully confirmed for transfer id: {}", transferDTO.getId());
            eventPublisher.publishEvent(transferConfirmed);

            log.info("carryOutTransfer toAccount: {}", accountRepo.findById(transferDTO.getToAccountId()).orElseThrow());
            log.info("carryOutTransfer from account: {}", accountRepo.findById(transferDTO.getFromAccountId()).orElseThrow());
        } catch (Exception e) {
            cancelTransfer(transferDTO);
            log.error("Transfer failed for transfer id: {}. Reversing operations.", transferDTO.getId(), e);
            eventPublisher.publishEvent(transferDTO);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected Transfer carryOutTransfer(TransferDTO transferDTO) {
        Account toAccount = accountRepo.findById(transferDTO.getToAccountId()).orElseThrow();

        BigDecimal transferAmount = transferDTO.getAmount();
        BigDecimal transferBonusAmount = transferDTO.getBonusAmount();
        BigDecimal newBalance = toAccount.getAvailableBalance().add(transferAmount).add(transferBonusAmount);
        toAccount.setBalance(newBalance);
        accountRepo.save(toAccount);

        Account fromAccount = accountRepo.findById(transferDTO.getFromAccountId()).orElseThrow();

        fromAccount.releaseFromHold(transferAmount);
        accountRepo.save(fromAccount);

        transferDTO.setStatus(TransferStatus.COMPLETED);
        transferDTO.setConfirmedAt(LocalDateTime.now());

        Transfer transfer = transferRepo.findById(transferDTO.getId()).orElseThrow();
        transfer.setFromAccount(fromAccount);
        transfer.setToAccount(toAccount);
        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setConfirmedAt(transferDTO.getConfirmedAt());

        return transferRepo.save(transfer);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void cancelTransfer(TransferDTO transferDTO) {

        Transfer transfer = transferMapper.toEntity(transferDTO);
        Account fromAccount = accountRepo.findById(transferDTO.getFromAccountId()).orElseThrow();
        transfer.setFromAccount(fromAccount);

        cancelTransfer(transfer);

    }

    @Override
    public void cancelPendingTransfers() {
        int page = 0;
        int size = 500;
        Page<Long> transferIds;

        do {
            transferIds = transferRepo.findTransferIdsWithStatusPendingAndCreatedAToToday(PageRequest.of(page, size));
            transferIds.forEach(transferId -> {
                try {
                    processSingleTransfer(transferId);
                } catch (Exception e) {
                    log.error("Error processing transfer cancel {}: {}", transferId, e.getMessage());
                }
            });
            page++;
        } while (transferIds.hasNext());
    }

    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class},
            backoff = @Backoff(delay = 100)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void processSingleTransfer(Long transferId) {
        transferRepo.findByIdForUpdate(transferId).ifPresent(transfer -> {

            transfer.setStatus(TransferStatus.FAILED);

            cancelTransfer(transfer);

        });
    }

    private void cancelTransfer(Transfer transfer) {

        BigDecimal transferAmount = transfer.getAmount();
        BigDecimal transferBonusAmount = transfer.getBonusAmount();

        Account fromAccount = transfer.getFromAccount();

        fromAccount.setBalance(fromAccount.getAvailableBalance().add(transferAmount));
        fromAccount.setBonusBalance(fromAccount.getAvailableBonusBalance().add(transferBonusAmount));

        fromAccount.releaseFromHold(transferAmount);
        fromAccount.releaseFromBonusHold(transferBonusAmount);

        accountRepo.save(fromAccount);

        transfer.setStatus(TransferStatus.FAILED);

        transferRepo.save(transfer);
    }

}
