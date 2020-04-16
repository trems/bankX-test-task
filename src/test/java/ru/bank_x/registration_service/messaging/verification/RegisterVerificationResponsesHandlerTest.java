package ru.bank_x.registration_service.messaging.verification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.GenericMessage;
import ru.bank_x.registration_service.data.UserRepository;
import ru.bank_x.registration_service.domain.User;
import ru.bank_x.registration_service.dto.RegisterVerificationRequest;
import ru.bank_x.registration_service.dto.RegisterVerificationResponse;
import ru.bank_x.registration_service.messaging.SendMailer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;

@SpringBootTest
class RegisterVerificationResponsesHandlerTest {

    @InjectMocks private RegisterVerificationResponsesHandler responsesHandler;
    @Mock private SendMailer sendMailer;
    @Autowired private UserRepository userRepository;
    private User user = new User("user", "password", "user@domain.ru",
            new User.FIO("Ivanov","Ivan", "Ivanovich"));
    private GenericMessage<RegisterVerificationResponse> responseMsg = new GenericMessage<>(new RegisterVerificationResponse(new RegisterVerificationRequest(user), true));

    @Autowired
    RegisterVerificationResponsesHandlerTest(RegisterVerificationResponsesHandler responsesHandler) {
        this.responsesHandler = responsesHandler;
    }


    @BeforeEach
    void setUp() throws TimeoutException {
        doNothing().when(sendMailer).sendMail(any());
    }

    @Test
    void handleMessage() {
        Assertions.assertTrue(responsesHandler.handleMessage(responseMsg));
    }

    @Test
    void sendMessageToEmail() throws TimeoutException, ExecutionException, InterruptedException {
        CompletableFuture<Boolean> emailSent = responsesHandler.sendMessageToEmail(responseMsg);
        Assertions.assertTrue(emailSent.get());
    }

    @Test
    void saveMessageToDB() {
        userRepository.deleteAll();
        userRepository.save(user);
        user.setVerified(true);
        responsesHandler.saveMessageToDB(responseMsg);
        User userFromDB = userRepository.findById(this.user.getLogin()).get();
        Assertions.assertTrue(userFromDB.isVerified());
        Assertions.assertTrue(userFromDB.isNotified());
    }

}