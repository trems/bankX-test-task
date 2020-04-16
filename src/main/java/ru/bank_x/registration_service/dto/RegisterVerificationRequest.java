package ru.bank_x.registration_service.dto;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import ru.bank_x.registration_service.domain.User;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.UUID;

/**
 * Неотработанные заявки на верификацию сохраняются в БД в случае отказа нашей системы
 */
@Value
@Entity
@AllArgsConstructor
@Table(name = "register_verify_req")
public class RegisterVerificationRequest {

    @Id
    String login;

    @NonFinal
    @Setter
    UUID requestId;

    @Transient
    String email;
    @Transient
    User.FIO fio;
    @Transient
    Date registeredAt;

    public RegisterVerificationRequest(User user) {
        this(user.getLogin(), null, user.getEmail(), user.getFio(), user.getRegisteredAt());
    }

    // Дефолтный конструктор для Hibernate
    private RegisterVerificationRequest() {
        this(null, null, null, null, null);
    }
}
