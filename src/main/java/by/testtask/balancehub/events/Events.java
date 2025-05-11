package by.testtask.balancehub.events;

import by.testtask.balancehub.domain.Transfer;
import by.testtask.balancehub.dto.elasticsearch.UserIndex;

public abstract class Events {

    public record UserChangedEvent(UserIndex index) {}

    public record TransferEvent(Transfer transfer) {}

    public record TransferConfirmed(Transfer transfer) {}

}
