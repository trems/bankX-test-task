package ru.bank_x.registration_service.messaging.verification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import ru.bank_x.registration_service.dto.RegisterVerificationRequest;
import ru.bank_x.registration_service.dto.RegisterVerificationResponse;
import ru.bank_x.registration_service.utils.RandomBehaviorUtils;
import ru.bank_x.registration_service.messaging.MessageId;
import ru.bank_x.registration_service.messaging.MessageListener;
import ru.bank_x.registration_service.messaging.ResponseSupplier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Обработчик сообщений с типом {@link RegisterVerificationRequest}.
 * Генерирует ответ {@link RegisterVerificationResponse} с результатом верификации пользователя.
 */
@Component
public class RegisterVerificationRequestsHandler implements MessageListener<RegisterVerificationRequest, MessageId>, ResponseSupplier<RegisterVerificationResponse, MessageId> {

    private RandomBehaviorUtils randomBehaviorUtils;

    private Map<MessageId, RegisterVerificationRequest> requests = new HashMap<>();

    @Autowired
    public RegisterVerificationRequestsHandler(RandomBehaviorUtils randomBehaviorUtils) {
        this.randomBehaviorUtils = randomBehaviorUtils;
    }

    @Override
    public MessageId handleMessage(Message<RegisterVerificationRequest> incomingMessage) throws TimeoutException {
        randomBehaviorUtils.simulateRandomSystemFailure();
        MessageId messageId = new MessageId(UUID.randomUUID());
        requests.put(messageId, incomingMessage.getPayload());
        return messageId;
    }

    @Override
    public RegisterVerificationResponse getResponse(MessageId requestId) {
        RegisterVerificationRequest request = requests.get(requestId);
        requests.remove(requestId);
        return new RegisterVerificationResponse(request, randomBehaviorUtils.isRegisterRequestApproved());
    }
}
