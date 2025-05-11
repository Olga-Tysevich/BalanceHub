package by.testtask.balancehub.events.listeners;
import by.testtask.balancehub.domain.Transfer;
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
    private final RedisTemplate<String, Transfer> redisTemplate;

    @EventListener
    public void handleTransferEvent(Events.TransferEvent event) {
        Transfer transfer = event.transfer();
        System.out.println("Transfer Event Received: " + transfer);
        redisTemplate.opsForList().leftPush(TRANSFER_QUEUE_NAME, transfer);
    }

    @EventListener
    public void handleTransferConfirmedEvent(Events.TransferConfirmed event) {
        Transfer transfer = event.transfer();
        System.out.println("Transfer Confirmed Event Received: " + transfer);
        redisTemplate.opsForList().leftPush(CONFIRMED_TRANSFER_QUEUE_NAME, transfer);
    }

}