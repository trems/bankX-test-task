package ru.bank_x.registration_service.messaging.verification;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.GenericMessage;
import ru.bank_x.registration_service.domain.User;
import ru.bank_x.registration_service.dto.RegisterVerificationRequest;
import ru.bank_x.registration_service.dto.RegisterVerificationResponse;
import ru.bank_x.registration_service.messaging.MessageId;
import ru.bank_x.registration_service.utils.RandomBehaviorUtils;

import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;

@SpringBootTest
class RegisterVerificationRequestsHandlerTest {

    @InjectMocks private RegisterVerificationRequestsHandler requestsHandler;
    @Mock private RandomBehaviorUtils randomBehaviorUtils;
    private User user = new User("user", "password", "user@domain.ru",
            new User.FIO("Ivanov","Ivan", "Ivanovich"));

    @BeforeEach
    void setUp() {
        when(randomBehaviorUtils.shouldSleep()).thenReturn(Boolean.FALSE);
        when(randomBehaviorUtils.shouldThrowTimeout()).thenReturn(Boolean.FALSE);
    }

    @Test
    void handleMessage() throws TimeoutException {
        MessageId messageId = requestsHandler.handleMessage(new GenericMessage<>(new RegisterVerificationRequest(user)));
        Assertions.assertThat(messageId).isNotNull();
    }

    @Test
    void getResponseWithNullRequest() {
        RegisterVerificationResponse response = requestsHandler.getResponse(new MessageId(UUID.randomUUID()));
        Assertions.assertThat(response.getRequest()).isNull();
    }

    @Test
    void getResponseWithValidRequest() throws TimeoutException {
        MessageId messageId = requestsHandler.handleMessage(new GenericMessage<>(new RegisterVerificationRequest(user)));
        RegisterVerificationResponse response = requestsHandler.getResponse(messageId);
        Assertions.assertThat(response).isNotNull();
    }

}