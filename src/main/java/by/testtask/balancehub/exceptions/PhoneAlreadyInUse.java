package by.testtask.balancehub.exceptions;

public class PhoneAlreadyInUse extends RuntimeException {

    public PhoneAlreadyInUse(String email) {
        super(String.format("Phone:%s already in use", email));
    }

    public PhoneAlreadyInUse(String email, String message) {
        super(message + "\nPhone: " + email);
    }

    public PhoneAlreadyInUse(String message, Throwable cause) {
        super(message, cause);
    }

    public PhoneAlreadyInUse(Throwable cause) {
        super(cause);
    }

    public PhoneAlreadyInUse(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
