package ru.bank_x.registration_service.messaging.verification;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import ru.bank_x.registration_service.data.UserRepository;
import ru.bank_x.registration_service.domain.User;
import ru.bank_x.registration_service.dto.RegisterVerificationRequest;
import ru.bank_x.registration_service.dto.RegisterVerificationResponse;
import ru.bank_x.registration_service.executors.TasksQueueExecutorLoop;
import ru.bank_x.registration_service.messaging.MessageListener;
import ru.bank_x.registration_service.messaging.MessageToDatabaseSaver;
import ru.bank_x.registration_service.messaging.MessageToEmailSender;
import ru.bank_x.registration_service.messaging.SendMailer;

import java.util.concurrent.*;
import java.util.stream.Stream;

/**
 * Обработчик сообщений с типом {@link RegisterVerificationResponse}.
 * Результат верификации сохраняет в БД и отправляет на email, имплементируя соответствующие интерфейсы.
 * Работает с очередью входящих сообщений через {@code taskExecutorLoop}.
 * При завершении работы бина сохраняет все неотправленные сообщения в БД, а при инициализации восстанавливает их оттуда.
 */
@Slf4j
@Component
public class RegisterVerificationResponsesHandler implements MessageListener<RegisterVerificationResponse, Boolean>, MessageToEmailSender<RegisterVerificationResponse>, MessageToDatabaseSaver<RegisterVerificationResponse>, InitializingBean, DisposableBean {

    private static final String MAIL_TEXT = "Уважаемый %s, Ваша заявка %s";
    private static final int QUEUE_SIZE = 500;
    private static final int TIMEOUT_SECONDS = 70;

    private UserRepository userRepository;
    private SendMailer sendMailer;

    private static BlockingQueue<Message<RegisterVerificationResponse>> messagesQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
    private TasksQueueExecutorLoop<Message<RegisterVerificationResponse>> taskExecutorLoop;

    @Autowired
    public RegisterVerificationResponsesHandler(UserRepository userRepository, SendMailer sendMailer) {
        this.userRepository = userRepository;
        this.sendMailer = sendMailer;
        this.taskExecutorLoop = new TasksQueueExecutorLoop<>(messagesQueue, Executors.newFixedThreadPool(4)) {
            @Override
            protected Runnable getTask(Message<RegisterVerificationResponse> item) {
                task = () -> {
                    try {
                        CompletableFuture<Boolean> mailSent = sendMessageToEmail(item);
                        mailSent.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                        saveMessageToDB(item);
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        messagesQueue.add(item);
                    }
                };
                return task;
            }
        };
    }

    @Override
    public Boolean handleMessage(Message<RegisterVerificationResponse> incomingMessage) {
        try {
            messagesQueue.put(incomingMessage);
            return true;
        } catch (InterruptedException e) {
            updateUserNotifiedAndVerifiedStatusInDB(incomingMessage, false);
            return false;
        }
    }

    /**
     * Генерирует письмо на основании сообщения {@code message} и отправляет его
     *
     * @return Boolean.TRUE внутри CompletableFuture если письмо было успешно отправлено
     * @throws TimeoutException если вызывающий поток был прерван во время ожидания таймаута отправки письма
     */
    @Override
    public CompletableFuture<Boolean> sendMessageToEmail(Message<RegisterVerificationResponse> message) throws TimeoutException {
        RegisterVerificationResponse response = message.getPayload();
        RegisterVerificationRequest originalRequest = response.getRequest();

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(originalRequest.getEmail());
        String mailText;
        if (response.isVerified()) {
            mailText = String.format(MAIL_TEXT, originalRequest.getFio(), "одобрена.");
        } else {
            mailText = String.format(MAIL_TEXT, originalRequest.getFio(), "отклонена.");
        }
        mail.setText(mailText);
        sendMailer.sendMail(mail);
        return CompletableFuture.completedFuture(Boolean.TRUE);
    }

    @Override
    public void saveMessageToDB(Message<RegisterVerificationResponse> message) {
        updateUserNotifiedAndVerifiedStatusInDB(message, true);
    }

    private void updateUserNotifiedAndVerifiedStatusInDB(Message<RegisterVerificationResponse> message, boolean notified) {
        RegisterVerificationResponse response = message.getPayload();
        RegisterVerificationRequest originalRequest = response.getRequest();
        userRepository.updateVerifiedAndNotifiedByLogin(originalRequest.getLogin(), response.isVerified(), notified);
        log.info("User's notified status [{}] was saved to to DB, id: {}", notified, originalRequest.getRequestId());
    }

    /**
     * Завершает работу {@code taskExecutorLoop}, таким образом заставляя его вернуть все неотправленные сообщения
     * обратно в {@code messagesQueue} для последующего сохранения в БД
     */
    @Override
    public void destroy() throws Exception {
        taskExecutorLoop.shutdown();
        messagesQueue.forEach(message -> updateUserNotifiedAndVerifiedStatusInDB(message, false));
        log.info("{} unsent messages from queue saved to DB", messagesQueue.size());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        restoreMessages();
        taskExecutorLoop.startWorkingLoop();
    }


    private void restoreMessages() {
        Iterable<User> notNotifiedUsers = userRepository.findAllByNotifiedAndVerifiedIsNotNull(false);
        notNotifiedUsers.forEach(user -> {

            RegisterVerificationRequest req = new RegisterVerificationRequest(user);
            RegisterVerificationResponse resp = new RegisterVerificationResponse(req, user.isVerified());
            try {
                messagesQueue.put(new GenericMessage<>(resp));
            } catch (InterruptedException e) {
                log.error("Thread interrupted while {} initialization", this.getClass().getSimpleName());
                Thread.currentThread().interrupt();
            }
        });
        log.info("{} unsent messages was restored from DB", messagesQueue.size());
    }
}
