package ru.bank_x.registration_service.messaging;

import ru.bank_x.registration_service.messaging.dto.Response;

/**
 * Сущность, которая поставляет ответы на заявки
 *
 * @param <T>  тип необходимого ответа
 * @param <ID> тип идентификатора заявки, для которой нужно предоставить ответ
 */
public interface ResponseSupplier<T extends Response, ID> {

    T getResponse(ID requestId);

}
