package ru.bank_x.registration_service.messaging;

import org.springframework.messaging.Message;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

/**
 * Сущность, которая может отправлять сообщения на email
 *
 * @param <T> тип, из которого будет сформировано письмо
 */
public interface MessageToEmailSender<T> {

    CompletableFuture<Boolean> sendMessageToEmail(Message<T> message) throws TimeoutException;

}
