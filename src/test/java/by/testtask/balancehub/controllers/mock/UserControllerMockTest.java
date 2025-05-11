package by.testtask.balancehub.controllers.mock;

import by.testtask.balancehub.controllers.BaseUITest;
import by.testtask.balancehub.dto.req.UserModificationRequests;
import by.testtask.balancehub.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static by.testtask.balancehub.utils.TestConstants.USER_CRED;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(UserControllerTest.MockConfig.class)
class UserControllerTest extends BaseUITest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String accessToken;

    @BeforeEach
    void setUp() {
        this.accessToken = getAccessToken(USER_CRED);
    }

    @Test
    void testAddEmail() throws Exception {
        UserModificationRequests.AddEmailRequest request = new UserModificationRequests.AddEmailRequest("test@example.com");
        when(userService.addEmail(request.email())).thenReturn(1L);

        mockMvc.perform(post("/v1/api/users/emails/add")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void testAddPhone() throws Exception {
        UserModificationRequests.AddPhoneRequest request = new UserModificationRequests.AddPhoneRequest("+1234567890");
        when(userService.addPhone(request.phone())).thenReturn(2L);

        mockMvc.perform(post("/v1/api/users/phones/add")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(2));
    }

    @Test
    void testChangeEmail() throws Exception {
        UserModificationRequests.ChangeEmailRequest request = new UserModificationRequests.ChangeEmailRequest(10L, "new@example.com");
        when(userService.changeEmail(request.oldEmailId(), request.newEmail())).thenReturn(3L);

        mockMvc.perform(put("/v1/api/users/emails/change")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(3));
    }

    @Test
    void testChangePhone() throws Exception {
        UserModificationRequests.ChangePhoneRequest request = new UserModificationRequests.ChangePhoneRequest(20L, "+0987654321");
        when(userService.changePhone(request.oldPhoneId(), request.newPhone())).thenReturn(4L);

        mockMvc.perform(put("/v1/api/users/phones/change")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(4));
    }

    @Test
    void testDeleteEmail() throws Exception {
        UserModificationRequests.DeleteEmailRequest request = new UserModificationRequests.DeleteEmailRequest(30L);
        when(userService.deleteEmail(request.emailId())).thenReturn(5L);

        mockMvc.perform(delete("/v1/api/users/emails/delete")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(5));
    }

    @Test
    void testDeletePhone() throws Exception {
        UserModificationRequests.DeletePhoneRequest request = new UserModificationRequests.DeletePhoneRequest(40L);
        when(userService.deletePhone(request.phoneId())).thenReturn(6L);

        mockMvc.perform(delete("/v1/api/users/phones/delete")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(6));
    }
}