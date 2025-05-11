package by.testtask.balancehub.events;

import by.testtask.balancehub.dto.elasticsearch.UserIndex;

public abstract class Events {

    public record UserChangedEvent(UserIndex index) {}

}
