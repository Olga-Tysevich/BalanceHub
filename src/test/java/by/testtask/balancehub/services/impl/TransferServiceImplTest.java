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
import java.util.List;

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
        Account fromAccount = accountRepo.findById(1L).orElseThrow();
        Account toAccount = accountRepo.findById(2L).orElseThrow();

        fromAccount.releaseFromHold(fromAccount.getHold());
        fromAccount.releaseFromBonusHold(fromAccount.getBonusHold());
        fromAccount.setBalance(new BigDecimal("200.00"));
        fromAccount.setBonusBalance(BigDecimal.ZERO);
        toAccount.setBalance(BigDecimal.ZERO);

        accountRepo.saveAllAndFlush(List.of(fromAccount, toAccount));

        BigDecimal initialTotal = fromAccount.getRawBalance()
                .add(toAccount.getRawBalance())
                .add(fromAccount.getRawBonusBalance())
                .add(toAccount.getRawBonusBalance());

        MoneyTransferReq transferRequest = new MoneyTransferReq();
        transferRequest.setFromAccountId(fromAccount.getId());
        transferRequest.setToAccountId(toAccount.getId());
        transferRequest.setAmount(new BigDecimal("50.00"));

        super.setAuthentication(USERNAME_1_EMAIL_LIST.getFirst(), USERNAME_1_PASSWORD);
        Long transferId = transferService.createTransfer(transferRequest);

        fromAccount = accountRepo.findById(1L).orElseThrow();
        assertThat(fromAccount.getHold()).isEqualTo(new BigDecimal("50.00"));
        assertThat(fromAccount.getAvailableBalance()).isEqualTo(new BigDecimal("150.00"));

        transferQueueProcessor.processQueue();

        fromAccount = accountRepo.findById(1L).orElseThrow();
        toAccount = accountRepo.findById(2L).orElseThrow();
        Transfer transferResult = transferRepo.findById(transferId).orElseThrow();

        assertThat(fromAccount.getRawBalance())
                .isEqualTo(new BigDecimal("150.00"));
        assertThat(fromAccount.getHold())
                .isEqualTo(BigDecimal.ZERO);
        assertThat(toAccount.getRawBalance())
                .isEqualTo(new BigDecimal("50.00"));

        BigDecimal finalTotal = fromAccount.getRawBalance()
                .add(toAccount.getRawBalance())
                .add(fromAccount.getRawBonusBalance())
                .add(toAccount.getRawBonusBalance());
        assertThat(finalTotal)
                .isEqualByComparingTo(initialTotal);
        assertThat(transferResult.getStatus())
                .isEqualTo(TransferStatus.COMPLETED);
        assertThat(transferResult.getAmount())
                .isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(transferResult.getConfirmedAt())
                .isNotNull();
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
