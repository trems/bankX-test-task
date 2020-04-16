package ru.bank_x.registration_service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.bank_x.registration_service.controllers.RegistrationController;
import ru.bank_x.registration_service.domain.User;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RegistrationControllerTest {

    private RegistrationController registrationController;
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private User userForMatching;
    private RequestEntity<String> newUserRequest;
    private String jsonRequest = "{\n" +
            "  \"login\": \"user\",\n" +
            "  \"password\": \"password\",\n" +
            "  \"confirmPassword\": \"password\",\n" +
            "  \"email\": \"%s\",\n" +
            "  \"surname\": \"Ivanov\",\n" +
            "  \"name\": \"Ivan\",\n" +
            "  \"patronymic\": \"Ivanovich\"\n" +
            "}";


    @Autowired
    public RegistrationControllerTest(RegistrationController registrationController, TestRestTemplate restTemplate) {
        this.registrationController = registrationController;
        this.restTemplate = restTemplate;
        this.userForMatching = new User("user", "password", "user@domain.ru",
                new User.FIO("Ivanov","Ivan", "Ivanovich"));
    }

    @BeforeEach
    void setUp() throws URISyntaxException {

    }

    @Test
    void contextLoads() {
        assertThat(registrationController).isNotNull();
    }

    @Test
    void shouldReturnSavedUser() throws URISyntaxException {
        makeValidUserRequest();
        ResponseEntity<User> userResponseEntity = makeNewUserRequest();
        assertEquals(this.userForMatching, userResponseEntity.getBody());
    }

    @Test
    void shouldReturnStatusCode500() throws URISyntaxException {
        makeInvalidUserRequest();
        ResponseEntity<User> userResponseEntity = makeNewUserRequest();
        assertThat(userResponseEntity.getStatusCodeValue()).isGreaterThanOrEqualTo(500);
    }

    private ResponseEntity<User> makeNewUserRequest() {
        return restTemplate.postForEntity(
                "http://localhost:" + port + "/register",
                newUserRequest,
                User.class);
    }

    private void makeValidUserRequest() throws URISyntaxException {
        HttpHeaders headers = getHttpHeaders();
        String validEmail = "user@domain.ru";
        jsonRequest = String.format(jsonRequest, validEmail);
        newUserRequest = new RequestEntity<>(jsonRequest, headers, HttpMethod.POST, new URI("http://localhost:" + port + "/register"));
    }

    private void makeInvalidUserRequest() throws URISyntaxException {
        HttpHeaders headers = getHttpHeaders();
        String invalidEmail = "user@domainru";
        jsonRequest = String.format(jsonRequest, invalidEmail);
        newUserRequest = new RequestEntity<>(jsonRequest, headers, HttpMethod.POST, new URI("http://localhost:" + port + "/register"));
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
}
