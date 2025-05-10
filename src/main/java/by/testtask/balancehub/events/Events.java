package by.testtask.balancehub.events;

import by.testtask.balancehub.domain.User;

public abstract class Events {

    public record UserChangedEvent(User user) {}

}
