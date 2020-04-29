package ru.bank_x.registration_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.bank_x.registration_service.domain.User;
import ru.bank_x.registration_service.messaging.dto.RegistrationForm;
import ru.bank_x.registration_service.services.registration.UserRegistrationServiceFacade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRegistrationServiceFacade userRegistrationServiceFacade;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User userForMatching = User.builder()
            .login("user")
            .email("user@domain.ru")
            .fio(new User.FIO("Ivanov", "Ivan", "Ivanovich"))
            .build();

    // Оставил плейсхолдер для имейла для проверки валидации
    private String jsonRequest = "{\n" +
            "  \"login\": \"user\",\n" +
            "  \"password\": \"password\",\n" +
            "  \"confirmPassword\": \"password\",\n" +
            "  \"email\": \"%s\",\n" +
            "  \"surname\": \"Ivanov\",\n" +
            "  \"name\": \"Ivan\",\n" +
            "  \"patronymic\": \"Ivanovich\"\n" +
            "}";

    @BeforeEach
    void setUp() {
        when(userRegistrationServiceFacade.registerUser(any(RegistrationForm.class))).thenReturn(userForMatching);
    }

    @Test
    void shouldReturnEmailValidationError() throws Exception {
        final String requestBodyWithInvalidEmail = String.format(jsonRequest, "userdomain.ru");
        mockMvc.perform(
                post("/register")
                        .content(requestBodyWithInvalidEmail)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email", Matchers.notNullValue()));
    }

    @Test
    void shouldReturnSuccessfullySavedUser() throws Exception {
        final String requestBodyWithValidEmail = String.format(jsonRequest, "user@domain.ru");
        String responseBody = mockMvc.perform(
                post("/register")
                        .content(requestBodyWithValidEmail)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println("Response: " + responseBody);
        User savedUser = objectMapper.readValue(responseBody, User.class);

        assertEquals(userForMatching, savedUser);
    }
}
