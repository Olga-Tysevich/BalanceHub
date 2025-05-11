package by.testtask.balancehub.utils;

import java.time.LocalDate;
import java.util.List;

public interface TestConstants {
    String SCHEME_SOURCE_PATH = "schemas/";
    String BASE_URL = "http://localhost:%s/v1/api";
    int DEFAULT_APP_PORT = 8080;
    long DEFAULT_TIMEOUT = 1500L;

    String USERNAME_1 = "Alice Johnson";
    String USERNAME_2 = "Bob Smith";
    String USERNAME_3 = "Charlie Brown";
    String USERNAME_4 = "Diana Prince";

    Long USERNAME_1_ID = 1L;
    Long USERNAME_2_ID = 2L;
    Long USERNAME_3_ID = 3L;
    Long USERNAME_4_ID = 4L;

    List<String> USERNAME_1_EMAIL_LIST = List.of("alice1@test.com", "alice2@test.com");
    List<String> USERNAME_2_EMAIL_LIST = List.of("bob1@test.com", "bob2@test.com");
    List<String> USERNAME_3_EMAIL_LIST = List.of("charlie1@test.com", "charlie2@test.com");
    List<String> USERNAME_4_EMAIL_LIST = List.of("diana1@test.com", "diana2@test.com");

    List<String> USERNAME_1_PHONE_LIST = List.of("79201111101", "79201111102");
    List<String> USERNAME_2_PHONE_LIST = List.of("79202222201", "792022222202");
    List<String> USERNAME_3_PHONE_LIST = List.of("79203333301", "792033333302");
    List<String> USERNAME_4_PHONE_LIST = List.of("79204444401", "79204444402");

    LocalDate USERNAME_1_DATE_OF_BIRTH_BEFORE = LocalDate.of(1985, 2,9);
    LocalDate USERNAME_3_DATE_OF_BIRTH_BEFORE = LocalDate.of(1982, 12,2);

    LocalDate USERNAME_1_DATE_OF_BIRTH = LocalDate.of(1985, 2,10);
    LocalDate USERNAME_2_DATE_OF_BIRTH = LocalDate.of(1990, 7,15);
    LocalDate USERNAME_3_DATE_OF_BIRTH = LocalDate.of(1982, 12,3);
    LocalDate USERNAME_4_DATE_OF_BIRTH = LocalDate.of(1995, 5,21);

    String USERNAME_1_PASSWORD = "passAlice1";
    String USERNAME_2_PASSWORD = "passBob12";
    String USERNAME_3_PASSWORD = "charlieP3";
    String USERNAME_4_PASSWORD = "dianaPa44";

    Long USERNAME_1_ACCOUNT_ID = 1L;
    Long USERNAME_2_ACCOUNT_ID = 2L;
    Long USERNAME_3_ACCOUNT_ID = 3L;
    Long USERNAME_4_ACCOUNT_ID = 4L;

    String USER_CRED = "{\"emailOrPhone\": \"" + USERNAME_2_EMAIL_LIST.getFirst() + "\", \"password\": \"" + USERNAME_2_PASSWORD + "\"}";
}
