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
import by.testtask.balancehub.repos.AccountRepo;
import by.testtask.balancehub.repos.TransferRepo;
import by.testtask.balancehub.services.AccountService;
import by.testtask.balancehub.utils.PrincipalExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class AccountServiceImpl implements AccountService {
    private final AccountRepo accountRepo;
    private final AccountMapper accountMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final TransferRepo transferRepo;
    private final TransferMapper transferMapper;

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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void makeTransfer(TransferDTO transferDTO) {
        Account toAccount = accountRepo.findById(transferDTO.getToAccountId()).orElseThrow();

        BigDecimal transferAmount = transferDTO.getAmount();
        BigDecimal newBalance = toAccount.getBalance().add(transferAmount);

        try {
            toAccount.setBalance(newBalance);
            accountRepo.save(toAccount);

            Account fromAccount = accountRepo.findById(transferDTO.getFromAccountId()).orElseThrow();
            fromAccount.setHold(fromAccount.getHold().subtract(transferAmount));
            accountRepo.save(fromAccount);

            transferDTO.setStatus(TransferStatus.CONFIRMED);
            transferDTO.setConfirmedAt(LocalDateTime.now());

            Transfer transfer = transferRepo.findById(transferDTO.getId()).orElseThrow();
            transfer.setFromAccount(fromAccount);
            transfer.setToAccount(toAccount);
            transfer.setStatus(TransferStatus.CONFIRMED);
            transfer.setConfirmedAt(transferDTO.getConfirmedAt());

            transferRepo.save(transfer);

            Events.TransferConfirmed transferConfirmed = new Events.TransferConfirmed(transferDTO);

            transferDTO.setStatus(transfer.getStatus());
            transferDTO.setConfirmedAt(transfer.getConfirmedAt());
            eventPublisher.publishEvent(transferConfirmed);

        } catch (Exception e) {

            Transfer transfer = transferMapper.toEntity(transferDTO);
            transfer.setStatus(TransferStatus.FAILED);

            transferRepo.save(transfer);

            transferDTO.setStatus(TransferStatus.FAILED);

            Account fromAccount = accountRepo.findById(transferDTO.getFromAccountId()).orElseThrow();
            fromAccount.setBalance(fromAccount.getBalance().add(transferAmount));
            fromAccount.setHold(fromAccount.getHold().subtract(transferAmount));

            accountRepo.save(fromAccount);

            eventPublisher.publishEvent(transferDTO);
        }
    }

    @Override
    public void createTransfer(MoneyTransferReq moneyTransferReq) {

        User currentUser = PrincipalExtractor.getCurrentUser();

        if (Objects.isNull(currentUser)) throw new UnauthorizedException();
        Long fromAccountId = moneyTransferReq.getFromAccountId();

        Long currentUserId = currentUser.getId();
        Optional<Long> accountOwnerId = accountRepo.findUserIdByAccountId(fromAccountId);

        if (accountOwnerId.isEmpty() || !accountOwnerId.get().equals(currentUserId)) {
            Long ownerId = accountOwnerId.orElseGet(() -> null);

            throw new ProhibitedException("The account owner is different from the current user. " +
                    "Owner id: " + ownerId + ", current user id: " + currentUserId);
        }

        BigDecimal amount = moneyTransferReq.getAmount();

        Optional<Account> fromAccountOpt = accountRepo.findByIdAndSufficientBalance(fromAccountId, amount);


        if (fromAccountOpt.isEmpty()) {
            throw new ProhibitedException("Insufficient balance: the balance is too low for this operation. Account id: " + fromAccountId);
        }

        Long toAccountId = moneyTransferReq.getToAccountId();
        Optional<Account> toAccountOpt = accountRepo.findById(toAccountId);

        if (toAccountOpt.isEmpty()) {
            throw new ProhibitedException("The specified recipient account does not exist!. Account id: " + toAccountOpt);
        }

        Account fromAccount = fromAccountOpt.get();
        Account toAccount = toAccountOpt.get();

        BigDecimal transferAmount = moneyTransferReq.getAmount();
        BigDecimal newAccountFromAmount = fromAccount.getBalance().subtract(transferAmount);
        fromAccount.setHold(transferAmount);
        fromAccount.setBalance(newAccountFromAmount);
        accountRepo.save(fromAccount);

        Transfer transfer = Transfer.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(amount)
                .status(TransferStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        transferRepo.save(transfer);

        TransferDTO transferDTO = transferMapper.toDTO(transfer);

        Events.TransferEvent transferEvent = new Events.TransferEvent(transferDTO);

        eventPublisher.publishEvent(transferEvent);
    }


}
