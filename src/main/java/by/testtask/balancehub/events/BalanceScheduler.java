package by.testtask.balancehub.events;

import by.testtask.balancehub.services.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BalanceScheduler {
    private final AccountService accountService;


    @Scheduled(fixedRate = 30000)
    public void increaseBalances() {
        accountService.safelyIncreaseBalance();
    }

}