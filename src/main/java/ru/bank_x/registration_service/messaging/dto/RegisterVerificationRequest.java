package ru.bank_x.registration_service.messaging.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.bank_x.registration_service.domain.User;

import java.util.Date;
import java.util.UUID;

/**
 * Неотработанные заявки на верификацию сохраняются в БД в случае отказа нашей системы
 */
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisterVerificationRequest {

    private final String login;

    private UUID requestId;

    String email;
    User.FIO fio;
    Date registeredAt;

    public RegisterVerificationRequest(User user) {
        this.login = user.getLogin();
        this.email = user.getEmail();
        this.fio = user.getFio();
        this.registeredAt = user.getRegisteredAt();
    }
}
