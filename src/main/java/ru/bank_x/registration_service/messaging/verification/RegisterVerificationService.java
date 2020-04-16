package ru.bank_x.registration_service.messaging.verification;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import ru.bank_x.registration_service.data.RegisterVerificationRequestRepository;
import ru.bank_x.registration_service.domain.User;
import ru.bank_x.registration_service.dto.RegisterVerificationRequest;
import ru.bank_x.registration_service.dto.RegisterVerificationResponse;
import ru.bank_x.registration_service.executors.TasksQueueExecutorLoop;
import ru.bank_x.registration_service.messaging.MessageId;
import ru.bank_x.registration_service.messaging.MessageListener;
import ru.bank_x.registration_service.messaging.MessagingService;

import java.util.concurrent.*;


/**
 * Сервис верификации пользователей.
 * Работает с очередью входящих заявок на верификацию через {@code taskExecutorLoop}.
 * При завершении работы бина сохраняет все заявки, которые не были отправлены или по которым не пришел ответ в БД, а при инициализации восстанавливает их оттуда.
 * Рукурсивно пытается отправить сообщение и получить ответ в течение {@code TIMEOUT_SECONDS}. Если за это время не удалось
 * получить ответ, помещает заявку на сообщение в конец очереди {@code requestsQueue}
 */
@Slf4j
@Service
public class RegisterVerificationService implements InitializingBean, DisposableBean {

    private static final String TIMEOUT_MSG = "Verification Service is not responding. Awaiting %s: %s";
    private static final int TIMEOUT_SECONDS = 70;
    private static final int QUEUE_SIZE = 500;

    private MessagingService<RegisterVerificationRequest, RegisterVerificationResponse> messagingService;
    private RegisterVerificationRequestRepository verificationRequestRepository;

    private static BlockingQueue<RegisterVerificationRequest> requestsQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
    private TasksQueueExecutorLoop<RegisterVerificationRequest> taskExecutorLoop;

    @Autowired
    public RegisterVerificationService(MessagingService<RegisterVerificationRequest, RegisterVerificationResponse> messagingService,
                                       MessageListener<RegisterVerificationResponse, Boolean> responsesListener,
                                       RegisterVerificationRequestRepository verificationRequestRepository) {
        this.messagingService = messagingService;
        this.verificationRequestRepository = verificationRequestRepository;
        taskExecutorLoop = new TasksQueueExecutorLoop<>(requestsQueue, Executors.newFixedThreadPool(4)) {
            @Override
            protected Runnable getTask(RegisterVerificationRequest item) {
                task = () -> {
                    try {
                        CompletableFuture<Message<RegisterVerificationResponse>> result = proceedRequest(item);
                        Message<RegisterVerificationResponse> response = result.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                        responsesListener.handleMessage(response);
                    } catch (TimeoutException | ExecutionException | InterruptedException e) {
                        requestsQueue.add(item);
                    }
                };
                return task;
            }
        };
    }

    /**
     * Помещает пользователя в очередь на верификацию
     */
    public void verifyUser(User user) {
        RegisterVerificationRequest request = new RegisterVerificationRequest(user);
        requestsQueue.add(request);
    }

    CompletableFuture<Message<RegisterVerificationResponse>> proceedRequest(RegisterVerificationRequest request) throws InterruptedException {
        MessageId messageId = null;
        if (request.getRequestId() == null) {
            messageId = sendRequestToVerify(request);
            request.setRequestId(messageId.getUuid());
            log.info("Request sent: {}", request);
        }

        Message<RegisterVerificationResponse> response = receiveResponse(messageId);
        log.info("Response received: [{}], id: {}", response.getPayload().isVerified(), messageId.getUuid());
        return CompletableFuture.completedFuture(response);
    }

    MessageId sendRequestToVerify(RegisterVerificationRequest request) throws InterruptedException {
        try {
            return messagingService.send(new GenericMessage<>(request));
        } catch (TimeoutException e) {
            log.error(String.format(TIMEOUT_MSG, "request", request));
            return sendRequestToVerify(request);
        }
    }

    Message<RegisterVerificationResponse> receiveResponse(MessageId messageId) throws InterruptedException {
        Message<RegisterVerificationResponse> respMessage;
        try {
            respMessage = messagingService.receive(messageId, RegisterVerificationResponse.class);
        } catch (TimeoutException e) {
            log.error(String.format(TIMEOUT_MSG, "response with id", messageId));
            return receiveResponse(messageId);
        }
        return respMessage;
    }

    /**
     * Завершает работу {@code taskExecutorLoop}, таким образом заставляя его вернуть все неотработанные заявки
     * обратно в {@code requestsQueue} для последующего сохранения в БД
     */
    @Override
    public void destroy() throws Exception {
        taskExecutorLoop.shutdown();
        verificationRequestRepository.saveAll(requestsQueue);
        log.info("{} verification requests from queue saved to DB", requestsQueue.size());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        restoreRequests();
        log.info("{} requests was restored from DB", requestsQueue.size());
        taskExecutorLoop.startWorkingLoop();
        log.info("Working loop started!");
    }

    /**
     * Исходя из условия, что любая часть системы может отказать,
     * будем восстанавливать неотправленные заявки на верификацию из БД.
     * C in-memory БД, конечно же, не работает.
     */
    private void restoreRequests() {
        for (RegisterVerificationRequest req : this.verificationRequestRepository.findAll()) {
            try {
                requestsQueue.put(req);
            } catch (InterruptedException e) {
                log.error("Thread interrupted while {} initialization", this.getClass().getSimpleName());
                Thread.currentThread().interrupt();
            }
        }
    }

    protected int getQueueSize() {
        return requestsQueue.size();
    }
}
