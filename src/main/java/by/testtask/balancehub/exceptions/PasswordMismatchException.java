package by.testtask.balancehub.exceptions;


import static by.testtask.balancehub.utils.Constants.PASSWORDS_MISMATCH;

public class PasswordMismatchException extends RuntimeException {

    public PasswordMismatchException() {
        super(PASSWORDS_MISMATCH);
    }

    public PasswordMismatchException(String message) {
        super(message);
    }

    public PasswordMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordMismatchException(Throwable cause) {
        super(cause);
    }

    public PasswordMismatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}