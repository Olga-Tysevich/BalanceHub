package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.BaseTest;
import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.repos.AccountRepo;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BalanceSchedulerTest extends BaseTest {

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private BalanceScheduler balanceScheduler;

    @Test
    void testIncreaseBalanceUpToLimit() {
        Account account = accountRepo.findById(1L).get();
        account.setBalance(new BigDecimal("100.00"));
        account.setInitialBalance(new BigDecimal("100.00"));
        accountRepo.save(account);

        balanceScheduler.increaseBalances();

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepo).save(accountCaptor.capture());

        Account updated = accountCaptor.getValue();
        assertEquals(new BigDecimal("110.00"), updated.getBalance());
    }

    @Test
    void testBalanceDoesNotExceedMaxLimit() {
        User user = new User();
        user.setId(2L);

        Account account = accountRepo.findById(1L).get();
        account.setBalance(new BigDecimal("300.00"));
        account.setInitialBalance(new BigDecimal("100.00"));
        accountRepo.save(account);

        balanceScheduler.increaseBalances();

        verify(accountRepo, never()).save(any());
    }

    @Test
    void testInitialBalanceIsZeroAndUpdated() {
        User user = new User();
        user.setId(3L);

        Account account = accountRepo.findById(3L).get();
        account.setBalance(new BigDecimal("100.00"));
        account.setInitialBalance(BigDecimal.ZERO);
        accountRepo.save(account);

        balanceScheduler.increaseBalances();

        verify(accountRepo).save(argThat(acc -> acc.getInitialBalance().equals(new BigDecimal("50.00"))));
    }
}