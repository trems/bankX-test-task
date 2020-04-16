package ru.bank_x.registration_service.messaging;


import org.springframework.messaging.Message;

import java.util.concurrent.TimeoutException;

/**
 * Ориентировочный интерфейс нашего messaging решения.
 *
 * @param <S> тип сообщения для отправки
 * @param <R> тип получаемого сообщения
 */
public interface MessagingService<S, R> {

    /**
     * Отправка сообщения в шину.
     *
     * @param msg сообщение для отправки.
     * @return идентификатор отправленного сообщения (correlationId)
     */
    MessageId send(Message<S> msg) throws TimeoutException;

    /**
     * Встает на ожидание ответа по сообщению с messageId.
     * Редко, но может кинуть исключение по таймауту.
     *
     * @param messageId   идентификатор сообщения, на которое ждем ответ.
     * @param messageType тип сообщения, к которому необходимо привести ответ.
     * @return Тело ответа.
     */
    Message<R> receive(MessageId messageId, Class<R> messageType) throws TimeoutException;
}