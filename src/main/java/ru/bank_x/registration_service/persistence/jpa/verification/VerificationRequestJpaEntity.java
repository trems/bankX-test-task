package ru.bank_x.registration_service.persistence.jpa.verification;

import lombok.*;
import lombok.experimental.NonFinal;
import ru.bank_x.registration_service.domain.User;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Неотработанные заявки на верификацию сохраняются в БД в случае отказа нашей системы
 */
@Data
@Builder
@Entity
@Table(name = "register_verify_req")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE, force = true)
class VerificationRequestJpaEntity {

    @Id
    private String login;

    @NonFinal
    @Setter
    private UUID requestId;

//    @Transient
//    private String email;
//    @Transient
//    private User.FIO fio;
//    @Transient
//    private Date registeredAt;

    public VerificationRequestJpaEntity(User user) {
        this(user.getLogin(), null);
//        this(user.getLogin(), null, user.getEmail(), user.getFio(), user.getRegisteredAt());
    }

    // Дефолтный конструктор для Hibernate
//    private VerificationRequestJpaEntity() {
//        this(null, null, null, null, null);
//    }
}

