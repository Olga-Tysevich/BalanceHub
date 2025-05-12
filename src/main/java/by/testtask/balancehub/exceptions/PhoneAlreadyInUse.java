package by.testtask.balancehub.exceptions;

public class PhoneAlreadyInUse extends RuntimeException {

    public PhoneAlreadyInUse(String email, String message) {
        super(message + "\nPhone: " + email);
    }

}
