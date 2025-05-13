package by.testtask.balancehub.events;
import by.testtask.balancehub.dto.redis.TransferDTO;
import by.testtask.balancehub.services.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static by.testtask.balancehub.utils.Constants.CONFIRMED_TRANSFER_QUEUE_NAME;
import static by.testtask.balancehub.utils.Constants.TRANSFER_QUEUE_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferQueueProcessor {
    private final AccountService accountService;
    private final RedisTemplate<String, TransferDTO> redisTemplate;

    @Scheduled(fixedRateString = "${spring.schedule.queueProcessor.fixedRate}")
    public void processQueue() {
        TransferDTO transferDTO = redisTemplate.opsForList().rightPop(TRANSFER_QUEUE_NAME);
        if (Objects.nonNull(transferDTO)) {
            log.info("Processing transferDTO from queue: {}", transferDTO);
            accountService.makeTransfer(transferDTO);
        }

        TransferDTO confirmedTransferDTO = redisTemplate.opsForList().rightPop(CONFIRMED_TRANSFER_QUEUE_NAME);
        if (Objects.nonNull(confirmedTransferDTO)) {
            //Тут можно слать уведомления юзеру или еще что-то
            log.info("Processing confirmed transferDTO from queue: {}", confirmedTransferDTO);
        }
    }
}