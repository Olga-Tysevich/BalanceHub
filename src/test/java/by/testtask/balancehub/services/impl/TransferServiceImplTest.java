package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.BaseTest;
import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.domain.Transfer;
import by.testtask.balancehub.domain.TransferStatus;
import by.testtask.balancehub.dto.req.MoneyTransferReq;
import by.testtask.balancehub.events.TransferQueueProcessor;
import by.testtask.balancehub.exceptions.ProhibitedException;
import by.testtask.balancehub.exceptions.UnauthorizedException;
import by.testtask.balancehub.repos.AccountRepo;
import by.testtask.balancehub.repos.TransferRepo;
import by.testtask.balancehub.services.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;

import static by.testtask.balancehub.utils.TestConstants.USERNAME_1_EMAIL_LIST;
import static by.testtask.balancehub.utils.TestConstants.USERNAME_1_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TransferServiceImplTest extends BaseTest {

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private TransferQueueProcessor transferQueueProcessor;

    @Autowired
    private TransferService transferService;

    @Autowired
    private TransferRepo transferRepo;

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.schedule.timing.increaseBalance.initialDelay", () -> 1_000_000);
    }

    @Test
    public void testTransfer_successfully() {
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
        Long transferId = transferService.createTransfer(transfer);

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
    public void testCreateTransfer_UnauthorizedUser_ThrowsUnauthorizedException() {
        MoneyTransferReq req = new MoneyTransferReq();
        req.setFromAccountId(1L);
        req.setToAccountId(2L);
        req.setAmount(BigDecimal.TEN);

        assertThatThrownBy(() -> transferService.createTransfer(req))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    public void testCreateTransfer_AccountOwnedByAnotherUser_ThrowsProhibitedException() {
        super.setAuthentication(USERNAME_1_EMAIL_LIST.getFirst(), USERNAME_1_PASSWORD);

        MoneyTransferReq req = new MoneyTransferReq();
        req.setFromAccountId(3L);
        req.setToAccountId(2L);
        req.setAmount(BigDecimal.TEN);

        assertThatThrownBy(() -> transferService.createTransfer(req))
                .isInstanceOf(ProhibitedException.class)
                .hasMessageContaining("account owner is different");
    }

    @Test
    public void testCreateTransfer_InsufficientBalance_ThrowsProhibitedException() {
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

        assertThatThrownBy(() -> transferService.createTransfer(req))
                .isInstanceOf(ProhibitedException.class)
                .hasMessageContaining("Insufficient balance");
    }

    @Test
    public void testCreateTransfer_ToAccountNotFound_ThrowsProhibitedException() {
        super.setAuthentication(USERNAME_1_EMAIL_LIST.getFirst(), USERNAME_1_PASSWORD);

        Account account = accountRepo.findById(1L).get();
        account.setBalance(new BigDecimal("100.00"));
        accountRepo.save(account);

        MoneyTransferReq req = new MoneyTransferReq();
        req.setFromAccountId(account.getId());
        req.setToAccountId(999L);
        req.setAmount(new BigDecimal("10.00"));

        assertThatThrownBy(() -> transferService.createTransfer(req))
                .isInstanceOf(ProhibitedException.class)
                .hasMessageContaining("recipient account does not exist");
    }

    @Test
    public void testTransfer_PartiallyFromBonusBalance() {
        super.setAuthentication(USERNAME_1_EMAIL_LIST.getFirst(), USERNAME_1_PASSWORD);

        Account fromAccount = accountRepo.findById(1L).get();
        fromAccount.setBalance(new BigDecimal("100.00"));
        fromAccount.setBonusBalance(new BigDecimal("30.00"));
        fromAccount.releaseFromHold(fromAccount.getHold());
        fromAccount.releaseFromBonusHold(fromAccount.getBonusHold());

        Account toAccount = accountRepo.findById(2L).get();
        toAccount.setBalance(BigDecimal.ZERO);
        toAccount.setBonusBalance(BigDecimal.ZERO);

        accountRepo.saveAndFlush(fromAccount);
        accountRepo.saveAndFlush(toAccount);

        MoneyTransferReq req = new MoneyTransferReq();
        req.setFromAccountId(fromAccount.getId());
        req.setToAccountId(toAccount.getId());
        req.setAmount(new BigDecimal("130.00"));

        Long transferId = transferService.createTransfer(req);

        fromAccount = accountRepo.findById(1L).get();

        assertThat(fromAccount.getHold()).isEqualTo(new BigDecimal("100.00"));
        assertThat(fromAccount.getBonusHold()).isEqualTo(new BigDecimal("30.00"));

        assertThat(fromAccount.getAvailableBonusBalance()).isEqualTo(BigDecimal.ZERO.setScale(2));
        assertThat(fromAccount.getAvailableBalance()).isEqualTo(BigDecimal.ZERO.setScale(2));

        transferQueueProcessor.processQueue();

        fromAccount = accountRepo.findById(1L).get();
        toAccount = accountRepo.findById(2L).get();

        assertThat(toAccount.getAvailableBalance()).isEqualTo(new BigDecimal("130.00"));
        assertThat(toAccount.getAvailableBonusBalance()).isEqualTo(BigDecimal.ZERO.setScale(2));

        assertThat(fromAccount.getHold()).isEqualTo(BigDecimal.ZERO.setScale(2));
        assertThat(fromAccount.getBonusHold()).isEqualTo(new BigDecimal("30.00"));

        Transfer transfer = transferRepo.findById(transferId).get();
        assertEquals(TransferStatus.COMPLETED, transfer.getStatus());
    }

}
