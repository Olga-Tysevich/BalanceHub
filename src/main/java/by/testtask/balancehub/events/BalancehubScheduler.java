package by.testtask.balancehub.events;

import by.testtask.balancehub.services.AccountService;
import by.testtask.balancehub.services.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BalancehubScheduler {
    private final AccountService accountService;
    private final TransferService transferService;

    @Scheduled(fixedRateString = "${spring.schedule.timing.increaseBalance}",
            initialDelayString = "${spring.schedule.timing.increaseBalance.initialDelay}")
    public void increaseBalances() {
        try {
            accountService.safelyIncreaseBalance();
        } catch (Exception e) {
            log.error("Exception occured while increasing balance: ", e);
        }
    }

    @Scheduled(fixedRateString = "${spring.schedule.timing.cancelTransfers}",
            initialDelayString = "${spring.schedule.timing.cancelTransfers.initialDelay}")
    public void cancelTransfers() {
        try {
            transferService.cancelPendingTransfers();
        } catch (Exception e) {
            log.error("Exception occured while cancelling transfers: ", e);
        }
    }

}