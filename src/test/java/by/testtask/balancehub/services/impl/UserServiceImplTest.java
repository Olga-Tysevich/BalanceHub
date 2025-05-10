package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.BaseTest;
import by.testtask.balancehub.dto.common.UserSearchType;
import by.testtask.balancehub.dto.req.UserSearchReq;
import by.testtask.balancehub.dto.resp.UserPageResp;
import by.testtask.balancehub.exceptions.EmailAlreadyInUse;
import by.testtask.balancehub.exceptions.UnauthorizedException;
import by.testtask.balancehub.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Map;

import static by.testtask.balancehub.utils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Test
    void addEmail_success() {
        String email = USERNAME_1_EMAIL.getFirst();

        Long userId = userService.addEmail(email);

        assertNotNull(userId);
        assertEquals(USERNAME_1_ID, userId);
    }

    @Test
    void addEmail_alreadyInUse_fails() {
        String email = USERNAME_1_EMAIL.getFirst();

        when(userService.addEmail(email)).thenThrow(new EmailAlreadyInUse(email));

        assertThrows(EmailAlreadyInUse.class, () -> userService.addEmail(email));
    }

    @Test
    void addPhone_success() {
        String phone = USERNAME_1_PHONE.getFirst();

        Long userId = userService.addPhone(phone);

        assertNotNull(userId);
        assertEquals(USERNAME_1_ID, userId);
    }

    @Test
    void addPhone_alreadyInUse_fails() {
        String phone = USERNAME_1_PHONE.getFirst();

        when(userService.addPhone(phone)).thenThrow(new EmailAlreadyInUse(phone));

        assertThrows(EmailAlreadyInUse.class, () -> userService.addPhone(phone));
    }

    @Test
    void changeEmail_success() {
        Long oldEmailId = 1L;
        String newEmail = "newemail@example.com";

        Long userId = userService.changeEmail(oldEmailId, newEmail);

        assertNotNull(userId);
        assertEquals(USERNAME_1_ID, userId);
    }

    @Test
    void changeEmail_emailInUse_fails() {
        Long oldEmailId = 1L;
        String newEmail = USERNAME_2_EMAIL.getFirst();

        assertThrows(EmailAlreadyInUse.class, () -> userService.changeEmail(oldEmailId, newEmail));
    }

    @Test
    void changePhone_success() {
        Long oldPhoneId = 1L;
        String newPhone = "79201111110";

        Long userId = userService.changePhone(oldPhoneId, newPhone);

        assertNotNull(userId);
        assertEquals(USERNAME_1_ID, userId);
    }

    @Test
    void changePhone_phoneInUse_fails() {
        Long oldPhoneId = 1L;
        String newPhone = USERNAME_2_PHONE.getFirst();

        assertThrows(EmailAlreadyInUse.class, () -> userService.changePhone(oldPhoneId, newPhone));
    }

    @Test
    void deleteEmail_success() {
        Long emailId = 1L;

        Long userId = userService.deleteEmail(emailId);

        assertNotNull(userId);
        assertEquals(USERNAME_1_ID, userId);
    }

    @Test
    void deleteEmail_notAuthorized_fails() {
        Long emailId = 1L;

        assertThrows(UnauthorizedException.class, () -> userService.deleteEmail(emailId));
    }

    @Test
    void deletePhone_success() {
        Long phoneId = 1L;

        Long userId = userService.deletePhone(phoneId);

        assertNotNull(userId);
        assertEquals(USERNAME_1_ID, userId);
    }

    @Test
    void deletePhone_notAuthorized_fails() {
        Long phoneId = 1L;

        assertThrows(UnauthorizedException.class, () -> userService.deletePhone(phoneId));
    }

    @Test
    void find_usersByAllParams_success() {
        UserSearchReq request = new UserSearchReq();
        request.setName(USERNAME_1);
        request.setPhone(USERNAME_1_PHONE.getFirst());
        request.setEmail(USERNAME_1_EMAIL.getFirst());
        request.setDateOfBirth(LocalDate.parse(USERNAME_1_DATE_OF_BIRTHDAY));
        request.setPage(0);
        request.setSize(10);

        Map<UserSearchType, UserPageResp> users = userService.find(request);

        assertNotNull(users);
        assertTrue(users.containsKey(UserSearchType.BY_ALL));
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
    }

    @Test
    void find_usersByEmail_success() {
        UserSearchReq request = new UserSearchReq();
        request.setPage(0);
        request.setSize(10);
        request.setName(USERNAME_1_EMAIL.getFirst());

        Map<UserSearchType, UserPageResp> users = userService.find(request);

        assertNotNull(users);
        assertTrue(users.containsKey(UserSearchType.BY_EMAIL));
    }

    @Test
    void find_usersByPhone_success() {
        UserSearchReq request = new UserSearchReq();
        request.setPage(0);
        request.setSize(10);
        request.setPhone(USERNAME_1_PHONE.getFirst());

        Map<UserSearchType, UserPageResp> users = userService.find(request);

        assertNotNull(users);
        assertTrue(users.containsKey(UserSearchType.BY_PHONE));
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
    }
}