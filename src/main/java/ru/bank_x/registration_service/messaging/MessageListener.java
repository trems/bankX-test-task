package ru.bank_x.registration_service.messaging;

import org.springframework.messaging.Message;

import java.util.concurrent.TimeoutException;

/**
 * Опциональный интерфейс для лисенеров.
 * Необязательно реализовывать всю инфраструктуру по регистрации и обработке, достаточно и тестов.
 *
 * @param <MsgType> тип сообщений, которые будем слушать.
 * @param <ReturnType> тип, который будем возвращать.
 */
public interface MessageListener<MsgType, ReturnType> {

    ReturnType handleMessage(Message<MsgType> incomingMessage) throws TimeoutException;

}