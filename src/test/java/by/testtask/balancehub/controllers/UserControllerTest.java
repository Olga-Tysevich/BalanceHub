package by.testtask.balancehub.controllers;

import by.testtask.balancehub.dto.common.UserDTO;
import by.testtask.balancehub.dto.req.UserSearchReq;
import by.testtask.balancehub.dto.resp.UserPageResp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static by.testtask.balancehub.utils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.type.TypeReference;


class UserControllerTest extends BaseUITest {

    @Test
    void shouldReturnValidUserPageResp_whenSearchingByPhone() throws JsonProcessingException {

        UserSearchReq requestBody = new UserSearchReq(
                USERNAME_3,
                USERNAME_3_EMAIL_LIST.getLast(),
                USERNAME_3_PHONE_LIST.getFirst(),
                LocalDate.parse(USERNAME_3_DATE_OF_BIRTHDAY),
                0,
                10
        );

        ValidatableResponse response = checkStatusCodeAndBodyInPostRequest(
                "/users/find",
                HttpStatus.OK.value(),
                SCHEME_SOURCE_PATH + "user_dto.json",
                requestBody,
                USER_CRED
        );


        String responseBody = response.extract().asString();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, UserPageResp> resultMap = objectMapper.readValue(
                responseBody,
                new TypeReference<>() {
                }
        );

        UserPageResp userPageResp = resultMap.get("BY_PHONE");
        assertNotNull(userPageResp, "Ответ по ключу BY_PHONE не найден");

        Set<UserDTO> users = userPageResp.getUsers();
        assertEquals(1, users.size(), "Ожидался один пользователь");

        assertEquals(1, userPageResp.getTotalUsers());
        assertEquals(1, userPageResp.getTotalPages());
    }
}