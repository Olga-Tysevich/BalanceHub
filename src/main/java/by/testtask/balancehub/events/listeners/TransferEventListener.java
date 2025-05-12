package by.testtask.balancehub.events.listeners;
import by.testtask.balancehub.dto.redis.TransferDTO;
import by.testtask.balancehub.events.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static by.testtask.balancehub.utils.Constants.CONFIRMED_TRANSFER_QUEUE_NAME;
import static by.testtask.balancehub.utils.Constants.TRANSFER_QUEUE_NAME;

//TODO логи
@Component
@RequiredArgsConstructor
public class TransferEventListener {
    private final RedisTemplate<String, TransferDTO> redisTemplate;

    @EventListener
    public void handleTransferEvent(Events.TransferEvent event) {
        TransferDTO transferDTO = event.transferDTO();
        System.out.println("TransferDTO Event Received: " + transferDTO);
        redisTemplate.opsForList().leftPush(TRANSFER_QUEUE_NAME, transferDTO);
    }

    @EventListener
    public void handleTransferConfirmedEvent(Events.TransferConfirmed event) {
        TransferDTO transferDTO = event.transferDTO();
        System.out.println("TransferDTO Confirmed Event Received: " + transferDTO);
        redisTemplate.opsForList().leftPush(CONFIRMED_TRANSFER_QUEUE_NAME, transferDTO);
    }

}