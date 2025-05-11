package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.BaseTest;
import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.dto.req.MoneyTransferReq;
import by.testtask.balancehub.repos.AccountRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static by.testtask.balancehub.utils.TestConstants.USERNAME_1_EMAIL_LIST;
import static by.testtask.balancehub.utils.TestConstants.USERNAME_1_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class AccountServiceIntegrationTest extends BaseTest {

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private TransferQueueProcessor transferQueueProcessor;

    @Autowired
    private AccountServiceImpl accountService;

    @Test
    void testTransferMoney() {
        Account fromAccount = accountRepo.findById(1L).get();

        super.setAuthentication(USERNAME_1_EMAIL_LIST.getFirst(), USERNAME_1_PASSWORD);

        fromAccount.setBalance(new BigDecimal("200.00"));
        fromAccount.setHold(BigDecimal.ZERO);

        Account toAccount = accountRepo.findById(2L).get();
        toAccount.setBalance(BigDecimal.ZERO);

        MoneyTransferReq transfer = new MoneyTransferReq();
        transfer.setFromAccountId(fromAccount.getId());
        transfer.setToAccountId(toAccount.getId());
        transfer.setAmount(new BigDecimal("50.00"));

        super.setAuthentication(USERNAME_1_EMAIL_LIST.getFirst(), USERNAME_1_PASSWORD);
        accountService.createTransfer(transfer);

        transferQueueProcessor.processQueue();

        fromAccount = accountRepo.findById(1L).get();
        toAccount = accountRepo.findById(2L).get();

        assertThat(toAccount.getBalance()).isEqualTo(new BigDecimal("50.00"));
        assertThat(fromAccount.getHold()).isEqualTo(new BigDecimal("50.00"));

    }
}
