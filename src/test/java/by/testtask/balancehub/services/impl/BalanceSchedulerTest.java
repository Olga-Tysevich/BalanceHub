package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.BaseTest;
import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.events.BalanceScheduler;
import by.testtask.balancehub.repos.AccountRepo;
import by.testtask.balancehub.repos.UserRepo;
import by.testtask.balancehub.utils.ObjectBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BalanceSchedulerTest extends BaseTest {

    @Value("${spring.application.interestRate}")
    protected BigDecimal interestRate;

    @Value("${spring.application.maxAllowedInterestRate}")
    protected BigDecimal maxAllowedInterestRate;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BalanceScheduler balanceScheduler;
    @Test
    void testBalanceIncreaseWithLimit() {
        Account account = accountRepo.findById(1L).get();
        BigDecimal initialBalance = account.getBalance();

        balanceScheduler.increaseBalances();
        account = accountRepo.findById(account.getId()).orElseThrow();
        BigDecimal expectedBalance = initialBalance.multiply(BigDecimal.ONE.add(interestRate));
        assertEquals(0, expectedBalance.compareTo(account.getBalance()), "Balances should match ignoring scale.");

        balanceScheduler.increaseBalances();
        account = accountRepo.findById(account.getId()).orElseThrow();
        expectedBalance = expectedBalance.multiply(BigDecimal.ONE.add(interestRate));
        assertEquals(0, expectedBalance.compareTo(account.getBalance()), "Balances should match ignoring scale.");

        BigDecimal almostMaxAllowedInterestRate = maxAllowedInterestRate.subtract(new BigDecimal("0.10"));
        BigDecimal balanceNearLimit = initialBalance.multiply(almostMaxAllowedInterestRate);
        account.setBalance(balanceNearLimit);
        accountRepo.save(account);

        balanceScheduler.increaseBalances();
        account = accountRepo.findById(account.getId()).orElseThrow();
        BigDecimal maxAllowedBalance = initialBalance.multiply(maxAllowedInterestRate);
        assertEquals(0, maxAllowedBalance.compareTo(account.getBalance()), "Balances should match ignoring scale.");

        balanceScheduler.increaseBalances();
        account = accountRepo.findById(account.getId()).orElseThrow();
        assertEquals(0, maxAllowedBalance.compareTo(account.getBalance()), "Balances should match ignoring scale.");
    }

    @Test
    void testZeroInitialBalance() {
        User user = ObjectBuilder.buildUser1();
        userRepo.saveAndFlush(user);

        Account zeroAccount = user.getAccount();
        zeroAccount = accountRepo.findById(zeroAccount.getId()).orElseThrow();

        balanceScheduler.increaseBalances();
        zeroAccount = accountRepo.findById(zeroAccount.getId()).orElseThrow();

        assertEquals(0, BigDecimal.ZERO.setScale(2).compareTo(zeroAccount.getBalance()), "Balance should be 0.00");
    }

    @Test
    void testNewAccountBalanceIncrease() {

        User user = ObjectBuilder.buildUser1();
        userRepo.saveAndFlush(user);

        Account newAccount = user.getAccount();

        balanceScheduler.increaseBalances();
        newAccount = accountRepo.findById(newAccount.getId()).orElseThrow();

        assertEquals(0, BigDecimal.ZERO.compareTo(newAccount.getBalance()), "The balance should remain zero initially.");

        BigDecimal depositAmount = new BigDecimal("50");
        newAccount.setBalance(depositAmount);

        accountRepo.saveAndFlush(newAccount);

        balanceScheduler.increaseBalances();
        newAccount = accountRepo.findById(newAccount.getId()).orElseThrow();

        BigDecimal expectedBalanceAfterFirstIncrease = depositAmount.multiply(BigDecimal.ONE.add(interestRate));
        assertEquals(1, expectedBalanceAfterFirstIncrease.compareTo(newAccount.getBalance()),
                "The balance should have increased according to the interest rate after deposit.");

        balanceScheduler.increaseBalances();
        newAccount = accountRepo.findById(newAccount.getId()).orElseThrow();
        BigDecimal expectedBalanceAfterSecondIncrease = expectedBalanceAfterFirstIncrease.multiply(BigDecimal.ONE.add(interestRate));

        assertEquals(expectedBalanceAfterSecondIncrease, newAccount.getBalance(),
                "The balance should have increased again according to the interest rate.");
    }

}