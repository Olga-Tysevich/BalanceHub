package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.BaseTest;
import by.testtask.balancehub.dto.req.UserLoginDTO;
import by.testtask.balancehub.dto.resp.LoggedUserDTO;
import by.testtask.balancehub.services.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceImplTestTest extends BaseTest {

    @Autowired
    private AuthService authService;

    @Test
    void loginUser_withEmail_success() {
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setEmailOrPhone(USERNAME_1_EMAIL.getLast());
        loginDTO.setPassword(USERNAME_1_PASSWORD);

        LoggedUserDTO loggedUser = authService.loginUser(loginDTO);

        assertNotNull(loggedUser);
        assertNotNull(loggedUser.getAccessToken());
    }

    @Test
    void loginUser_withPhone_success() {
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setEmailOrPhone(USERNAME_1_PHONE.getFirst());
        loginDTO.setPassword(USERNAME_1_PASSWORD);

        LoggedUserDTO loggedUser = authService.loginUser(loginDTO);

        assertNotNull(loggedUser);
        assertNotNull(loggedUser.getRefreshToken());
    }

    @Test
    void loginUser_withWrongPassword_fails() {
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setEmailOrPhone(USERNAME_1_EMAIL.getFirst());
        loginDTO.setPassword("wrongPassword");

        assertThrows(Exception.class, () -> authService.loginUser(loginDTO));
    }
}