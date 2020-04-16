package ru.bank_x.registration_service.messaging.verification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.GenericMessage;
import ru.bank_x.registration_service.data.RegisterVerificationRequestRepository;
import ru.bank_x.registration_service.data.UserRepository;
import ru.bank_x.registration_service.domain.User;
import ru.bank_x.registration_service.dto.RegisterVerificationRequest;
import ru.bank_x.registration_service.dto.RegisterVerificationResponse;
import ru.bank_x.registration_service.messaging.MessageId;
import ru.bank_x.registration_service.messaging.MessagingService;

import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class RegisterVerificationServiceTest {

    @InjectMocks private RegisterVerificationService registerVerificationService;
    @Mock private MessagingService messagingService;
    private UserRepository userRepository;
    private RegisterVerificationRequestRepository requestRepository;
    private User user = new User("user", "password", "user@domain.ru",
                                 new User.FIO("Ivanov","Ivan", "Ivanovich"));
    private RegisterVerificationRequest request = new RegisterVerificationRequest(user);

    @Autowired
    public RegisterVerificationServiceTest(RegisterVerificationService registerVerificationService, UserRepository userRepository, RegisterVerificationRequestRepository requestRepository) {
        this.registerVerificationService = registerVerificationService;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    @BeforeEach
    void setUp() throws TimeoutException {
        userRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.save(user);

        MessageId msgId;
        when(messagingService.send(any())).thenReturn(msgId = new MessageId(UUID.randomUUID()));

        RegisterVerificationResponse response = new RegisterVerificationResponse(request, true);
        when(messagingService.receive(msgId, RegisterVerificationResponse.class)).thenReturn(new GenericMessage(response));
    }

    @Test
    void verifyUser() throws InterruptedException {
        registerVerificationService.verifyUser(user);
        Thread.sleep(500);
        User userFromDb = userRepository.findById(user.getLogin()).get();
        assertThat(userFromDb.isVerified()).isTrue();
    }
}