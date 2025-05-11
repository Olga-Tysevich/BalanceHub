package by.testtask.balancehub.exceptions;

public class EmailAlreadyInUse extends RuntimeException {

    public EmailAlreadyInUse(String email) {
        super(String.format("Email:%s already in use", email));
    }

    public EmailAlreadyInUse(String email, String message) {
        super(message + "\nEmail: " + email);
    }

    public EmailAlreadyInUse(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailAlreadyInUse(Throwable cause) {
        super(cause);
    }

    public EmailAlreadyInUse(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
