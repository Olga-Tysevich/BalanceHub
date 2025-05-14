package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.BaseTest;
import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.domain.Transfer;
import by.testtask.balancehub.domain.TransferStatus;
import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.events.BalancehubScheduler;
import by.testtask.balancehub.repos.*;
import by.testtask.balancehub.utils.ObjectBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BalancehubSchedulerTest extends BaseTest {

    @Value("${spring.application.interestRate}")
    protected BigDecimal interestRate;

    @Value("${spring.application.maxAllowedInterestRate}")
    protected BigDecimal maxAllowedInterestRate;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BalancehubScheduler balancehubScheduler;

    @Autowired
    private TransferRepo transferRepo;

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.schedule.timing.increaseBalance.initialDelay", () -> 1_000_000);
        registry.add("spring.schedule.timing.cancelTransfers.initialDelay", () -> 1_000_000);
    }

    @Test
    void testBalanceIncreaseWithLimit() {
        Account account = accountRepo.findById(1L).get();
        BigDecimal depositAmount = new BigDecimal("1000");
        account.setBalance(depositAmount);
        account.setInitialBalance(depositAmount);
        account.setBonusBalance(BigDecimal.ZERO);

        accountRepo.saveAndFlush(account);

        BigDecimal initialBalance = account.getAvailableBalance();

        balancehubScheduler.increaseBalances();
        account = accountRepo.findById(account.getId()).orElseThrow();

        BigDecimal expectedBalance = initialBalance.multiply(BigDecimal.ONE.add(interestRate));
        assertEquals(0, expectedBalance.compareTo(account.getAvailableBonusBalance()), "Balances should match ignoring scale. " +
                "Expected: " + expectedBalance + ". Actual: " + account.getAvailableBonusBalance());

        balancehubScheduler.increaseBalances();
        account = accountRepo.findById(account.getId()).orElseThrow();
        expectedBalance = expectedBalance.multiply(BigDecimal.ONE.add(interestRate));
        assertEquals(0, expectedBalance.compareTo(account.getAvailableBonusBalance()), "Balances should match ignoring scale. " +
                "Expected: " + expectedBalance + ". Actual: " + account.getAvailableBonusBalance());

        BigDecimal almostMaxAllowedInterestRate = maxAllowedInterestRate.subtract(new BigDecimal("0.01"));
        BigDecimal balanceNearLimit = initialBalance.multiply(almostMaxAllowedInterestRate);
        account.setBonusBalance(balanceNearLimit);
        accountRepo.save(account);

        balancehubScheduler.increaseBalances();
        account = accountRepo.findById(account.getId()).orElseThrow();

        BigDecimal maxAllowedBalance = initialBalance.multiply(maxAllowedInterestRate);
        assertEquals(0, maxAllowedBalance.compareTo(account.getAvailableBonusBalance()), "Balances should match ignoring scale. " +
                "Expected: " + maxAllowedBalance + ". Actual: " + account.getAvailableBonusBalance());

        balancehubScheduler.increaseBalances();
        account = accountRepo.findById(account.getId()).orElseThrow();
        assertEquals(0, maxAllowedBalance.compareTo(account.getAvailableBonusBalance()), "Balances should match ignoring scale. " +
                "Expected: " + maxAllowedBalance + ". Actual: " + account.getAvailableBonusBalance());
    }

    @Test
    void testZeroInitialBalance() {
        User user = ObjectBuilder.buildUser1();
        userRepo.saveAndFlush(user);

        Account zeroAccount = user.getAccount();
        zeroAccount = accountRepo.findById(zeroAccount.getId()).orElseThrow();

        balancehubScheduler.increaseBalances();
        zeroAccount = accountRepo.findById(zeroAccount.getId()).orElseThrow();

        assertEquals(0, BigDecimal.ZERO.setScale(2).compareTo(zeroAccount.getAvailableBonusBalance()), "Balance should be 0.00. " +
                "Expected: " + BigDecimal.ZERO.setScale(2) + ". Actual: " + zeroAccount.getAvailableBonusBalance());
    }

    @Test
    void testNewAccountBalanceIncrease() {

        User user = ObjectBuilder.buildUser1();
        userRepo.saveAndFlush(user);

        Account newAccount = user.getAccount();

        balancehubScheduler.increaseBalances();
        newAccount = accountRepo.findById(newAccount.getId()).orElseThrow();

        assertEquals(0, BigDecimal.ZERO.compareTo(newAccount.getAvailableBonusBalance()), "The balance should remain zero initially. " +
                "Expected: " + BigDecimal.ZERO.setScale(2) + ". Actual: " + newAccount.getAvailableBonusBalance());

        BigDecimal depositAmount = new BigDecimal("50");
        newAccount.setBalance(depositAmount);

        accountRepo.saveAndFlush(newAccount);
        newAccount = accountRepo.findById(newAccount.getId()).orElseThrow();

        balancehubScheduler.increaseBalances();
        newAccount = accountRepo.findById(newAccount.getId()).orElseThrow();

        BigDecimal expectedBalanceAfterFirstIncrease = depositAmount.multiply(BigDecimal.ONE.add(interestRate));

        assertEquals(0, expectedBalanceAfterFirstIncrease.compareTo(newAccount.getAvailableBonusBalance()),
                "The balance should have increased again according to the interest rate. " +
                        "Expected: " + expectedBalanceAfterFirstIncrease + ". Actual: " + newAccount.getAvailableBonusBalance());
    }


    @Test
    void testCancelPendingTransfers() {
        User fromUser = userRepo.findById(1L).get();
        Account fromAccount = fromUser.getAccount();
        fromAccount.setBalance(new BigDecimal("1000.00"));
        fromAccount.setBonusBalance(new BigDecimal("200.00"));
        fromAccount.setInitialBalance(new BigDecimal("1000.00"));

        User toUser = userRepo.findById(2L).get();
        Account toAccount = toUser.getAccount();
        toAccount.setBalance(BigDecimal.ZERO);
        toAccount.setBonusBalance(BigDecimal.ZERO);
        toAccount.setInitialBalance(BigDecimal.ZERO);

        accountRepo.saveAndFlush(fromAccount);
        accountRepo.saveAndFlush(toAccount);

        BigDecimal transferAmount = new BigDecimal("300.00");
        BigDecimal bonusAmount = new BigDecimal("50.00");

        Transfer transfer = Transfer.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(transferAmount)
                .bonusAmount(bonusAmount)
                .status(TransferStatus.PENDING)
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        transferRepo.saveAndFlush(transfer);

        fromAccount = accountRepo.findById(fromAccount.getId()).orElseThrow();
        fromAccount.addToHold(transferAmount);
        fromAccount.addToBonusHold(bonusAmount);
        accountRepo.saveAndFlush(fromAccount);

        BigDecimal fromAccountExpectedBalance = fromAccount.getRawBalance();
        BigDecimal fromAccountExpectedBonus = fromAccount.getRawBonusBalance();
        BigDecimal fromAccountExpectedAvailableBalance = fromAccountExpectedBalance.subtract(transferAmount);
        BigDecimal fromAccountExpectedAvailableBonus = fromAccountExpectedBonus.subtract(bonusAmount);

        Account preCancel = accountRepo.findById(fromAccount.getId()).orElseThrow();
        assertEquals(0, fromAccountExpectedAvailableBalance.compareTo(preCancel.getAvailableBalance()));
        assertEquals(0, fromAccountExpectedAvailableBonus.compareTo(preCancel.getAvailableBonusBalance()));

        transfer.setCreatedAt(LocalDateTime.now().minusDays(1));
        transferRepo.saveAndFlush(transfer);

        balancehubScheduler.cancelTransfers();

        Transfer updatedTransfer = transferRepo.findById(transfer.getId()).orElseThrow();
        assertEquals(TransferStatus.FAILED, updatedTransfer.getStatus(), "Transfer should be marked as FAILED");

        Account updatedFromAccount = accountRepo.findById(fromAccount.getId()).orElseThrow();

        BigDecimal fromAccountActualBalance = updatedFromAccount.getAvailableBalance();
        BigDecimal fromAccountActualBonus = updatedFromAccount.getAvailableBonusBalance();

        toAccount = accountRepo.findById(toAccount.getId()).orElseThrow();
        BigDecimal toAccountActualBalance = toAccount.getAvailableBalance();
        BigDecimal toAccountActualBonus = toAccount.getAvailableBonusBalance();

        assertEquals(0, fromAccountActualBalance.compareTo(fromAccountExpectedBalance),
                "Raw balance for from account should reflect the hold release");
        assertEquals(0, fromAccountActualBonus.compareTo(fromAccountExpectedBonus),
                "Raw bonus balance for from account should reflect the hold release");


        assertEquals(0, BigDecimal.ZERO.setScale(2).compareTo(toAccountActualBalance),
                "Raw balance for to account should reflect the hold release");
        assertEquals(0, BigDecimal.ZERO.setScale(2).compareTo(toAccountActualBonus),
                "Raw bonus balance for to account should reflect the hold release");

        assertEquals(0, updatedFromAccount.getHold().compareTo(BigDecimal.ZERO.setScale(2)), "Hold for from account should be zero");
        assertEquals(0, updatedFromAccount.getBonusHold().compareTo(BigDecimal.ZERO.setScale(2)), "Bonus hold for from account should be zero");

        assertEquals(0, updatedFromAccount.getAvailableBalance().compareTo(fromAccountActualBalance), "Available = Raw after release  for from account.");
        assertEquals(0, updatedFromAccount.getAvailableBonusBalance().compareTo(fromAccountActualBonus), "Available bonus = Raw after release for from account.");
    }

}