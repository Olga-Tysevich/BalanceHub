package by.testtask.balancehub.events.listeners;
import by.testtask.balancehub.dto.redis.TransferDTO;
import by.testtask.balancehub.events.Events;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static by.testtask.balancehub.utils.Constants.CONFIRMED_TRANSFER_QUEUE_NAME;
import static by.testtask.balancehub.utils.Constants.TRANSFER_QUEUE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferEventListener {
    private final RedisTemplate<String, TransferDTO> redisTemplate;

    @EventListener
    public void handleTransferEvent(Events.TransferEvent event) {
        TransferDTO transferDTO = event.transferDTO();
        log.info("Received TransferEvent: TransferDTO [id={}, amount={}, from={}, to={}]",
                transferDTO.getId(), transferDTO.getAmount(), transferDTO.getFromUserId(), transferDTO.getToUserId());

        try {
            redisTemplate.opsForList().leftPush(TRANSFER_QUEUE_NAME, transferDTO);
            log.info("TransferDTO successfully pushed to the Redis transfer queue.");
        } catch (Exception e) {
            log.error("Failed to push TransferDTO to Redis queue: {}", e.getMessage(), e);
        }

    }

    @EventListener
    public void handleTransferConfirmedEvent(Events.TransferConfirmed event) {
        TransferDTO transferDTO = event.transferDTO();

        log.info("Received TransferConfirmed event: TransferDTO [id={}, amount={}, from={}, to={}]",
                transferDTO.getId(), transferDTO.getAmount(), transferDTO.getFromUserId(), transferDTO.getToUserId());

        try {
            redisTemplate.opsForList().leftPush(CONFIRMED_TRANSFER_QUEUE_NAME, transferDTO);
            log.info("TransferDTO successfully pushed to the Redis confirmed transfer queue.");
        } catch (Exception e) {
            log.error("Failed to push TransferDTO to Redis confirmed transfer queue: {}", e.getMessage(), e);
        }
    }

}