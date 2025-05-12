package by.testtask.balancehub.services.impl;
import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.dto.elasticsearch.UserIndexDTO;
import by.testtask.balancehub.events.Events;
import by.testtask.balancehub.mappers.UserMapper;
import by.testtask.balancehub.repos.AccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BalanceScheduler {
    @Value("${spring.application.interestRate}")
    private BigDecimal interestRate;

    @Value("${spring.application.maxAllowedInterestRate}")
    private BigDecimal maxAllowedInterestRate;

    private final ApplicationEventPublisher eventPublisher;

    private final UserMapper userMapper;

    private final AccountRepo accountRepo;

    private final CacheManager cacheManager;

    @Scheduled(fixedRate = 30000)
    public void increaseBalances() {
        List<Account> accounts = accountRepo.findAll();
        for (Account account : accounts) {

            BigDecimal currentBalance = account.getBalance();
            BigDecimal initialBalance = account.getInitialBalance();
            BigDecimal maxIncreaseBalance = BigDecimal.ONE.add(maxAllowedInterestRate).multiply(initialBalance);

            if (currentBalance.compareTo(maxIncreaseBalance) > 0) continue;

            if (BigDecimal.ZERO.compareTo(maxIncreaseBalance) == 0 && BigDecimal.ZERO.compareTo(currentBalance) < 0) {

                initialBalance = account.getBalance();
                account.setInitialBalance(initialBalance);
                saveAccount(account);
                continue;
            }

            BigDecimal newBalance = account.getBalance().multiply(BigDecimal.ONE.add(interestRate));


            if (newBalance.compareTo(maxIncreaseBalance) > 0)  {
                account.setBalance(maxIncreaseBalance);
            } else {
                account.setBalance(newBalance);
            }
            saveAccount(account);

        }
    }

    private void saveAccount(Account account) {
        accountRepo.save(account);

        User user = account.getUser();
        Long userId = user.getId();
        UserIndexDTO index = userMapper.toUserIndex(user);

        Optional.ofNullable(cacheManager.getCache("users"))
                .ifPresent(cache -> cache.evict(userId));

        eventPublisher.publishEvent(new Events.UserChangedEvent(index));
    }

}