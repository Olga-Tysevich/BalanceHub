package by.testtask.balancehub.utils;

public interface Constants {
    String TOKEN_HEADER = "Authorization";
    String TOKEN_TYPE = "Bearer";
    String USER_CLAIM_KEY = "USER_ID";
    String REFRESH_TOKEN_KEY = "refresh-token";

    String REGEXP_EMAIL = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    String REGEXP_PHONE = "^[0-9]{11}$";

    String TRANSFER_QUEUE_NAME = "transferQueue";
    String CONFIRMED_TRANSFER_QUEUE_NAME = "transferConfirmedQueue";

    String ID_CANNOT_BE_NULL = "ID cannot be null!";
    String USER_ID_CANNOT_BE_NULL = "User id must be specified";
    String USER_CANNOT_BE_NULL = "User must be specified";
    String BALANCE_CANNOT_BE_NULL = "Balance cannot be null";
    String NAME_CANNOT_BE_EMPTY = "Name cannot be null or empty!";
    String NAME_CANNOT_BE_GZ_500 = "Name cannot exceed 500 characters";
    String DATE_OF_BIRTH_MUST_BE_IN_PAST = "Date of birth must be in the past";
    String INVALID_PASSWORD_LENGTH = "Password must be between 8 and 500 characters";
    String INVALID_EMAIL_MESSAGE = "Invalid email format. Valid email example: example@domain.com";
    String EMAIL_CANNOT_BE_NULL_OR_EMPTY = "Email cannot be null or empty!";
    String PHONE_CANNOT_BE_NULL_OR_EMPTY = "Phone cannot be null or empty!";
    String INVALID_PHONE_FORMAT = "Phone number must be exactly 11 digits. Example: 79207865432";
    String EMPTY_PHONE_SET = "User must have between 1 and 10 phones.";
    String EMPTY_EMAIL_SET = "User must have between 1 and 5 email addresses.";
    String PASSWORD_CANNOT_BE_NULL_OR_EMPTY = "Password cannot be null or empty!";
    String ACCESS_DENIED = "Access Denied";
    String ACCESS_DENIED_MESSAGE = "User do not have permission to access this resource.";
    String NOT_AUTHORIZED = "Access Denied";
    String UNKNOWN_USER = "Unknown index";
    String TOKEN_CANNOT_BE_NULL_OR_EMPTY = "Token cannot be null or empty!";
    String INVALID_REFRESH_TOKEN = "Invalid refresh token!";
    String BALANCE_MUST_BE_POSITIVE = "Balance must be positive or zero!";
    String HOLD_MUST_BE_POSITIVE = "Hold balance must be positive!";
    String HOLD_CANNOT_BE_NULL = "Hold balance cannot be null";
    String TRANSFER_DATE_MUST_BE_IN_PAST = "Transfer date must be in the past";
    String TRANSFER_STATUS_CANNOT_BE_NUL = "Transfer cannot be null";
    String TRANSFER_AMOUNT_BE_POSITIVE = "Hold balance must be positive!";
    String ACCOUNT_CANNOT_BE_NULL = "Account must be specified";
    String VERSION_CANNOT_BE_NULL = "Version must be specified";
    String INITIAL_BALANCE_MUST_BE_POSITIVE = "Initial balance must be positive or zero!";
    String BONUS_BALANCE_MUST_BE_POSITIVE = "Balance must be positive or zero!";

}
