package by.testtask.balancehub.exceptions;

import org.springframework.security.access.AccessDeniedException;

public class ProhibitedException extends AccessDeniedException {
    public ProhibitedException(String message) {
        super(message);
    }
}
