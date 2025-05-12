package by.testtask.balancehub.exceptions;

public class EmailAlreadyInUse extends RuntimeException {

    public EmailAlreadyInUse(String email) {
        super(String.format("Email:%s already in use", email));
    }

    public EmailAlreadyInUse(String email, String message) {
        super(message + "\nEmail: " + email);
    }
}
