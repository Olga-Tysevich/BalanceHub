package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.BaseTest;
import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.domain.Transfer;
import by.testtask.balancehub.domain.TransferStatus;
import by.testtask.balancehub.dto.common.AccountDTO;
import by.testtask.balancehub.dto.req.MoneyTransferReq;
import by.testtask.balancehub.events.TransferQueueProcessor;
import by.testtask.balancehub.exceptions.ProhibitedException;
import by.testtask.balancehub.exceptions.UnauthorizedException;
import by.testtask.balancehub.repos.AccountRepo;
import by.testtask.balancehub.repos.TransferRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static by.testtask.balancehub.utils.TestConstants.USERNAME_1_EMAIL_LIST;
import static by.testtask.balancehub.utils.TestConstants.USERNAME_1_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class AccountServiceImplTest extends BaseTest {

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private TransferQueueProcessor transferQueueProcessor;

    @Autowired
    private AccountServiceImpl accountService;

    @Autowired
    private TransferRepo transferRepo;

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.schedule.timing.increaseBalance.initialDelay", () -> 1_000_000);
    }

    @Test
    void testTransfer_successfully() {
        Account fromAccount = accountRepo.findById(1L).get();

        super.setAuthentication(USERNAME_1_EMAIL_LIST.getFirst(), USERNAME_1_PASSWORD);

        fromAccount.releaseFromHold(fromAccount.getHold());
        fromAccount.releaseFromBonusHold(fromAccount.getBonusHold());

        fromAccount.setInitialBalance(new BigDecimal("200.00"));
        fromAccount.setBalance(new BigDecimal("200.00"));
        fromAccount.setBonusBalance(BigDecimal.ZERO);

        Account toAccount = accountRepo.findById(2L).get();
        toAccount.setBalance(BigDecimal.ZERO);

        accountRepo.saveAndFlush(fromAccount);
        accountRepo.saveAndFlush(toAccount);

        MoneyTransferReq transfer = new MoneyTransferReq();
        transfer.setFromAccountId(fromAccount.getId());
        transfer.setToAccountId(toAccount.getId());
        transfer.setAmount(new BigDecimal("50.00"));

        super.setAuthentication(USERNAME_1_EMAIL_LIST.getFirst(), USERNAME_1_PASSWORD);
        Long transferId = accountService.createTransfer(transfer);

        fromAccount = accountRepo.findById(1L).get();

        assertThat(fromAccount.getHold()).isEqualTo(new BigDecimal("50.00"));
        assertThat(fromAccount.getAvailableBalance()).isEqualTo(new BigDecimal("150.00"));

        transferQueueProcessor.processQueue();

        toAccount = accountRepo.findById(2L).get();

        assertThat(toAccount.getAvailableBalance()).isEqualTo(new BigDecimal("50.00"));
        assertThat(fromAccount.getHold()).isEqualTo(new BigDecimal("50.00"));
        assertThat(fromAccount.getAvailableBalance()).isEqualTo(new BigDecimal("150.00"));

        Transfer transferResult = transferRepo.findById(transferId).get();

        assertNotNull(transferResult.getId(), "Unexpected transfer id");
        assertEquals(fromAccount.getId(), transferResult.getFromAccount().getId(), "Unexpected transfer fromAccount");
        assertEquals(toAccount.getId(), transferResult.getToAccount().getId(), "Unexpected transfer toAccount");
        assertEquals(0, transfer.getAmount().compareTo(transferResult.getAmount()), "Unexpected transfer amount");
        assertEquals(TransferStatus.COMPLETED, transferResult.getStatus(), "Unexpected transfer status");
        assertNotNull(transferResult.getCreatedAt(), "Unexpected transfer createdAt");
        assertNotNull(transferResult.getConfirmedAt(), "Unexpected transfer confirmedAt");
        assertEquals(1L, transferResult.getVersion(), "Unexpected transfer version");

    }

    @Test
    void testCreateTransfer_UnauthorizedUser_ThrowsUnauthorizedException() {
        MoneyTransferReq req = new MoneyTransferReq();
        req.setFromAccountId(1L);
        req.setToAccountId(2L);
        req.setAmount(BigDecimal.TEN);

        assertThatThrownBy(() -> accountService.createTransfer(req))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void testCreateTransfer_AccountOwnedByAnotherUser_ThrowsProhibitedException() {
        super.setAuthentication(USERNAME_1_EMAIL_LIST.getFirst(), USERNAME_1_PASSWORD);

        MoneyTransferReq req = new MoneyTransferReq();
        req.setFromAccountId(3L);
        req.setToAccountId(2L);
        req.setAmount(BigDecimal.TEN);

        assertThatThrownBy(() -> accountService.createTransfer(req))
                .isInstanceOf(ProhibitedException.class)
                .hasMessageContaining("account owner is different");
    }

    @Test
    void testCreateTransfer_InsufficientBalance_ThrowsProhibitedException() {
        super.setAuthentication(USERNAME_1_EMAIL_LIST.getFirst(), USERNAME_1_PASSWORD);

        Account account = accountRepo.findById(1L).get();
        account.releaseFromHold(account.getHold());
        account.releaseFromBonusHold(account.getBonusHold());

        account.setBonusBalance(BigDecimal.ZERO);
        account.setBalance(new BigDecimal("5.00"));
        accountRepo.save(account);

        MoneyTransferReq req = new MoneyTransferReq();
        req.setFromAccountId(account.getId());
        req.setToAccountId(2L);
        req.setAmount(new BigDecimal("10.00"));

        assertThatThrownBy(() -> accountService.createTransfer(req))
                .isInstanceOf(ProhibitedException.class)
                .hasMessageContaining("Insufficient balance");
    }

    @Test
    void testCreateTransfer_ToAccountNotFound_ThrowsProhibitedException() {
        super.setAuthentication(USERNAME_1_EMAIL_LIST.getFirst(), USERNAME_1_PASSWORD);

        Account account = accountRepo.findById(1L).get();
        account.setBalance(new BigDecimal("100.00"));
        accountRepo.save(account);

        MoneyTransferReq req = new MoneyTransferReq();
        req.setFromAccountId(account.getId());
        req.setToAccountId(999L);
        req.setAmount(new BigDecimal("10.00"));

        assertThatThrownBy(() -> accountService.createTransfer(req))
                .isInstanceOf(ProhibitedException.class)
                .hasMessageContaining("recipient account does not exist");
    }

    @Test
    void testCreateAccount_AccountAlreadyExists_ThrowsRuntimeException() {
        super.setAuthentication(USERNAME_1_EMAIL_LIST.getFirst(), USERNAME_1_PASSWORD);

        Account existing = accountRepo.findById(1L).get();

        AccountDTO dto = new AccountDTO();
        dto.setId(existing.getId());
        dto.setUserId(existing.getUser().getId());

        assertThatThrownBy(() -> accountService.createAccount(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Account already exists");
    }

    @Test
    void testCreateAccount_DifferentUser_ThrowsAccessDeniedException() {
        super.setAuthentication(USERNAME_1_EMAIL_LIST.getFirst(), USERNAME_1_PASSWORD);

        AccountDTO dto = new AccountDTO();
        dto.setId(9999L);
        dto.setUserId(999L);

        assertThatThrownBy(() -> accountService.createAccount(dto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not allowed to modify this account");
    }

}
