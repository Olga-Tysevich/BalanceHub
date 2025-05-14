package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.BaseTest;
import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.events.BalanceScheduler;
import by.testtask.balancehub.repos.*;
import by.testtask.balancehub.utils.ObjectBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

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

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.schedule.timing.increaseBalance.initialDelay", () -> 1_000_000);
    }

    @Test
    void testBalanceIncreaseWithLimit() {
        Account account = accountRepo.findById(1L).get();
        BigDecimal depositAmount = new BigDecimal("1000");
        account.setBalance(depositAmount);
        account.setInitialBalance(depositAmount);

        accountRepo.saveAndFlush(account);

        BigDecimal initialBalance = account.getBalance();

        balanceScheduler.increaseBalances();
        account = accountRepo.findById(account.getId()).orElseThrow();

        BigDecimal expectedBalance = initialBalance.multiply(BigDecimal.ONE.add(interestRate));
        assertEquals(0, expectedBalance.compareTo(account.getBonusBalance()), "Balances should match ignoring scale. "+
                "Expected: " + expectedBalance + ". Actual: " + account.getBonusBalance());

        balanceScheduler.increaseBalances();
        account = accountRepo.findById(account.getId()).orElseThrow();
        expectedBalance = expectedBalance.multiply(BigDecimal.ONE.add(interestRate));
        assertEquals(0, expectedBalance.compareTo(account.getBonusBalance()), "Balances should match ignoring scale. "+
                "Expected: " + expectedBalance + ". Actual: " + account.getBonusBalance());

        BigDecimal almostMaxAllowedInterestRate = maxAllowedInterestRate.subtract(new BigDecimal("0.01"));
        BigDecimal balanceNearLimit = initialBalance.multiply(almostMaxAllowedInterestRate);
        account.setBonusBalance(balanceNearLimit);
        accountRepo.save(account);

        balanceScheduler.increaseBalances();
        account = accountRepo.findById(account.getId()).orElseThrow();

        BigDecimal maxAllowedBalance = initialBalance.multiply(maxAllowedInterestRate);
        assertEquals(0, maxAllowedBalance.compareTo(account.getBonusBalance()), "Balances should match ignoring scale. " +
                "Expected: " + maxAllowedBalance + ". Actual: " + account.getBonusBalance());

        balanceScheduler.increaseBalances();
        account = accountRepo.findById(account.getId()).orElseThrow();
        assertEquals(0, maxAllowedBalance.compareTo(account.getBonusBalance()), "Balances should match ignoring scale. "+
                "Expected: " + maxAllowedBalance + ". Actual: " + account.getBonusBalance());
    }

    @Test
    void testZeroInitialBalance() {
        User user = ObjectBuilder.buildUser1();
        userRepo.saveAndFlush(user);

        Account zeroAccount = user.getAccount();
        zeroAccount = accountRepo.findById(zeroAccount.getId()).orElseThrow();

        balanceScheduler.increaseBalances();
        zeroAccount = accountRepo.findById(zeroAccount.getId()).orElseThrow();

        assertEquals(0, BigDecimal.ZERO.setScale(2).compareTo(zeroAccount.getBonusBalance()), "Balance should be 0.00. "+
                "Expected: " + BigDecimal.ZERO.setScale(2) + ". Actual: " + zeroAccount.getBonusBalance());
    }

    @Test
    void testNewAccountBalanceIncrease() {

        User user = ObjectBuilder.buildUser1();
        userRepo.saveAndFlush(user);

        Account newAccount = user.getAccount();

        balanceScheduler.increaseBalances();
        newAccount = accountRepo.findById(newAccount.getId()).orElseThrow();

        assertEquals(0, BigDecimal.ZERO.compareTo(newAccount.getBonusBalance()), "The balance should remain zero initially. "+
                "Expected: " + BigDecimal.ZERO.setScale(2) + ". Actual: " + newAccount.getBonusBalance());

        BigDecimal depositAmount = new BigDecimal("50");
        newAccount.setBalance(depositAmount);

        accountRepo.saveAndFlush(newAccount);
        newAccount = accountRepo.findById(newAccount.getId()).orElseThrow();

        balanceScheduler.increaseBalances();
        newAccount = accountRepo.findById(newAccount.getId()).orElseThrow();

        BigDecimal expectedBalanceAfterFirstIncrease = depositAmount.multiply(BigDecimal.ONE.add(interestRate));

        assertEquals(0, expectedBalanceAfterFirstIncrease.compareTo(newAccount.getBonusBalance()),
                "The balance should have increased again according to the interest rate. "+
                        "Expected: " + expectedBalanceAfterFirstIncrease + ". Actual: " + newAccount.getBonusBalance());
    }

}