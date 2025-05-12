package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.BaseTest;
import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.repos.AccountRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BalanceSchedulerTest extends BaseTest {

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private BalanceScheduler balanceScheduler;

    @Test
    void testIncreaseBalanceUpToLimit() {
        Account account = accountRepo.findById(1L).get();
        BigDecimal balance = account.getBalance();

        balanceScheduler.increaseBalances();;
        assertEquals(balance, account.getBalance());
    }

}