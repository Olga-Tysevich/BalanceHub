package by.testtask.balancehub.events;

import by.testtask.balancehub.dto.elasticsearch.UserIndexDTO;
import by.testtask.balancehub.dto.redis.TransferDTO;

public abstract class Events {

    public record UserChangedEvent(UserIndexDTO index) {}

    public record TransferEvent(TransferDTO transferDTO) {}

    public record TransferConfirmed(TransferDTO transferDTO) {}

}
