package ru.bank_x.registration_service.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import ru.bank_x.registration_service.messaging.dto.Response;
import ru.bank_x.registration_service.utils.RandomBehaviorUtils;

import java.util.Collections;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class MessagingServiceStub<S, R extends Response> implements MessagingService<S, R> {

    private RandomBehaviorUtils randomBehaviorUtils;
    private MessageListener<S, MessageId> requestsListener;
    private ResponseSupplier<R, MessageId> responseSupplier;

    @Autowired
    public MessagingServiceStub(RandomBehaviorUtils randomBehaviorUtils, MessageListener<S, MessageId> requestsListener, ResponseSupplier<R, MessageId> responseSupplier) {
        this.randomBehaviorUtils = randomBehaviorUtils;
        this.requestsListener = requestsListener;
        this.responseSupplier = responseSupplier;
    }

    @Override
    public MessageId send(Message<S> msg) throws TimeoutException {
        return requestsListener.handleMessage(msg);
    }

    @Override
    public Message<R> receive(MessageId messageId, Class<R> messageType) throws TimeoutException {
        randomBehaviorUtils.simulateRandomSystemFailure();

        R payload = responseSupplier.getResponse(messageId);

        // return our stub message here.
        return new GenericMessage<>(payload, Collections.singletonMap("messageId", messageId));
    }
}
