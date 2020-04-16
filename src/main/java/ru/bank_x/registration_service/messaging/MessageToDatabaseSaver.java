package ru.bank_x.registration_service.messaging;

import org.springframework.messaging.Message;

/**
 * Сущность, которая может сохранять сообщения в БД
 *
 * @param <T> тип, информация из которого будет сохранена в БД
 */
public interface MessageToDatabaseSaver<T> {

    void saveMessageToDB(Message<T> message);
}
