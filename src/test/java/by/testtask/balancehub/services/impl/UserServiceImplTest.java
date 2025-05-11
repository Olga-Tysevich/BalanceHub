package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.BaseTest;
import by.testtask.balancehub.dto.common.UserDTO;
import by.testtask.balancehub.dto.common.UserSearchType;
import by.testtask.balancehub.dto.req.UserSearchReq;
import by.testtask.balancehub.dto.resp.UserPageResp;
import by.testtask.balancehub.exceptions.EmailAlreadyInUse;
import by.testtask.balancehub.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static by.testtask.balancehub.utils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest extends BaseTest {

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        setAuthentication(USERNAME_1_EMAIL_LIST.getFirst(), USERNAME_1_PASSWORD);
    }

    @Test
    void addEmail_success() {
        String email = "mynewemail@gmail.com";

        Long userId = userService.addEmail(email);

        assertNotNull(userId);
        assertEquals(USERNAME_1_ID, userId);
    }

    @Test
    void addEmail_alreadyInUse_fails() {
        String email = USERNAME_1_EMAIL_LIST.getFirst();

        assertThrows(EmailAlreadyInUse.class, () -> userService.addEmail(email));
    }

    @Test
    void addPhone_success() {
        String phone = "79201111111";

        Long userId = userService.addPhone(phone);

        assertNotNull(userId);
        assertEquals(USERNAME_1_ID, userId);
    }

    @Test
    void addPhone_alreadyInUse_fails() {
        String phone = USERNAME_1_PHONE_LIST.getFirst();

        assertThrows(EmailAlreadyInUse.class, () -> userService.addPhone(phone));
    }

    @Test
    void changeEmail_success() {
        Long oldEmailId = 5L;

        setAuthentication(USERNAME_3_EMAIL_LIST.getFirst(), USERNAME_3_PASSWORD);

        String newEmail = "newemail@example.com";

        Long userId = userService.changeEmail(oldEmailId, newEmail);

        assertNotNull(userId);
        assertEquals(USERNAME_3_ID, userId);
    }

    @Test
    void changeEmail_emailInUse_fails() {
        Long oldEmailId = 1L;
        String newEmail = USERNAME_2_EMAIL_LIST.getFirst();

        assertThrows(EmailAlreadyInUse.class, () -> userService.changeEmail(oldEmailId, newEmail));
    }

    @Test
    void changePhone_success() {
        Long oldPhoneId = 5L;
        String newPhone = "79201111110";

        setAuthentication(USERNAME_3_EMAIL_LIST.getFirst(), USERNAME_3_PASSWORD);

        Long userId = userService.changePhone(oldPhoneId, newPhone);

        assertNotNull(userId);
        assertEquals(USERNAME_3_ID, userId);
    }

    @Test
    void changePhone_phoneInUse_fails() {
        Long oldPhoneId = 1L;
        String newPhone = USERNAME_2_PHONE_LIST.getFirst();

        assertThrows(EmailAlreadyInUse.class, () -> userService.changePhone(oldPhoneId, newPhone));
    }

    @Test
    void deleteEmail_success() {
        Long emailId = 8L;

        setAuthentication(USERNAME_4_EMAIL_LIST.getFirst(), USERNAME_4_PASSWORD);

        Long userId = userService.deleteEmail(emailId);

        assertNotNull(userId);
        assertEquals(USERNAME_4_ID, userId);
    }

    @Test
    void deleteEmail_notAuthorized_fails() {
        Long emailId = 1L;

        clearAuthentication();

        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> userService.deleteEmail(emailId));
    }

    @Test
    void deletePhone_success() {
        setAuthentication(USERNAME_4_PHONE_LIST.getFirst(), USERNAME_4_PASSWORD);

        Long phoneId = 8L;

        Long userId = userService.deletePhone(phoneId);

        assertNotNull(userId);
        assertEquals(USERNAME_4_ID, userId);
    }

    @Test
    void deletePhone_notAuthorized_fails() {
        Long phoneId = 1L;

        clearAuthentication();

        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> userService.deletePhone(phoneId));
    }

    @Test
    void find_usersByAllParams_success() {
        UserSearchReq request = new UserSearchReq();
        request.setName(USERNAME_1);
        request.setPhone(USERNAME_1_PHONE_LIST.getFirst());
        request.setEmail(USERNAME_1_EMAIL_LIST.getFirst());
        request.setDateOfBirth(LocalDate.parse(USERNAME_1_DATE_OF_BIRTHDAY_DAY_BEFORE));
        request.setPage(0);
        request.setSize(10);

        Map<UserSearchType, UserPageResp> users = userService.find(request);

        assertNotNull(users);
        assertTrue(users.containsKey(UserSearchType.BY_ALL));

        Set<UserDTO> userSet = users.get(UserSearchType.BY_ALL).getUsers();

        assertNotNull(userSet);
        assertEquals(1, userSet.size());
    }

    @Test
    void find_usersByName_success() {
        UserSearchReq request = new UserSearchReq();
        request.setPage(0);
        request.setSize(10);
        request.setName(USERNAME_1);

        Map<UserSearchType, UserPageResp> users = userService.find(request);

        assertNotNull(users);
        assertTrue(users.containsKey(UserSearchType.BY_NAME));

        Set<UserDTO> userSet = users.get(UserSearchType.BY_NAME).getUsers();

        assertNotNull(userSet);
        assertEquals(1, userSet.size());
    }

    @Test
    void find_usersByEmail_success() {
        UserSearchReq request = new UserSearchReq();
        request.setPage(0);
        request.setSize(10);
        request.setEmail(USERNAME_1_EMAIL_LIST.getFirst());

        Map<UserSearchType, UserPageResp> users = userService.find(request);

        assertNotNull(users);
        assertTrue(users.containsKey(UserSearchType.BY_EMAIL));

        Set<UserDTO> userSet = users.get(UserSearchType.BY_EMAIL).getUsers();

        assertNotNull(userSet);
        assertEquals(1, userSet.size());
    }

    @Test
    void find_usersByPhone_success() {
        UserSearchReq request = new UserSearchReq();
        request.setPage(0);
        request.setSize(10);
        request.setPhone(USERNAME_1_PHONE_LIST.getFirst());

        Map<UserSearchType, UserPageResp> users = userService.find(request);

        assertNotNull(users);
        assertTrue(users.containsKey(UserSearchType.BY_PHONE));

        Set<UserDTO> userSet = users.get(UserSearchType.BY_PHONE).getUsers();

        assertNotNull(userSet);
        assertEquals(1, userSet.size());
    }

    @Test
    void find_usersByDateOfBirth_success() {
        UserSearchReq request = new UserSearchReq();
        request.setPage(0);
        request.setSize(10);
        request.setDateOfBirth(LocalDate.parse(USERNAME_1_DATE_OF_BIRTHDAY));

        Map<UserSearchType, UserPageResp> users = userService.find(request);

        assertNotNull(users);
        assertTrue(users.containsKey(UserSearchType.BY_BIRTHDAY));

        Set<UserDTO> userSet = users.get(UserSearchType.BY_BIRTHDAY).getUsers();

        assertNotNull(userSet);
        assertEquals(6, userSet.size());
    }
}