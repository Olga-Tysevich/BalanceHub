package by.testtask.balancehub.events;

import by.testtask.balancehub.dto.common.UserDTO;

public abstract class Events {

    public record UserChangedEvent(UserDTO dto) {}

}
