package by.testtask.balancehub.utils;

public interface Constants {
    String DATE_OF_BIRTHDAY_PATTERN = "dd.MM.yyyy";
    String REGEXP_EMAIL = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    String REGEXP_PHONE = "^[0-9]{11}$";

    String ID_CANNOT_BE_NULL = "ID cannot be null!";
    String USER_CANNOT_BE_NULL = "User must be specified";
    String BALANCE_CANNOT_BE_NULL = "Balance cannot be null";
    String NAME_CANNOT_BE_EMPTY = "Name cannot be null or empty!";
    String NAME_CANNOT_BE_GZ_500 = "Name cannot exceed 500 characters";
    String DATE_OF_BIRTHDAY_MUST_BE_IN_PAST = "Date of birth must be in the past";
    String INVALID_PASSWORD_LENGTH = "Password must be between 8 and 500 characters";
    String INVALID_EMAIL_MESSAGE = "Invalid email format. Valid email example: example@domain.com";
    String EMAIL_CANNOT_BE_NULL_OR_EMPTY = "Email cannot be null or empty!";
    String PHONE_CANNOT_BE_NULL_OR_EMPTY = "Phone cannot be null or empty!";
    String INVALID_PHONE_FORMAT = "Phone number must be exactly 11 digits. Example: 79207865432";
    String EMPTY_PHONE_SET = "User must have between 1 and 10 phones.";
    String EMPTY_EMAIL_SET = "User must have between 1 and 5 email addresses.";

}
