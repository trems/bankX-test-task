package ru.bank_x.registration_service.domain;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@EqualsAndHashCode
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class User {

    @Id
    private final String login;

    @EqualsAndHashCode.Exclude
    private final String password;

    private final String email;

    @Embedded
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private final FIO fio;

    @EqualsAndHashCode.Exclude
    private Date registeredAt;

    @PrePersist
    void registeredAt() {
        this.registeredAt = new Date();
    }

    @Nullable
    private boolean verified;
    @Nullable
    private boolean notified;


    @Data
    @Entity
    @Embeddable
    @Table(name = "fio")
    @EqualsAndHashCode
    @RequiredArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PACKAGE, force = true)
    public static class FIO {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @EqualsAndHashCode.Exclude
        private Long id;

        private final String surname;
        private final String name;
        private final String patronymic;

        @Override
        public String toString() {
            return String.join(" ", surname, name, patronymic);
        }

    }
}
