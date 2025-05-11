package by.testtask.balancehub.exceptions;

import static by.testtask.balancehub.utils.Constants.INVALID_REFRESH_TOKEN;

public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException() {
        super(INVALID_REFRESH_TOKEN);
    }

}