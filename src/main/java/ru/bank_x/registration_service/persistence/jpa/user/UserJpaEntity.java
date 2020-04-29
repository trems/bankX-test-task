package ru.bank_x.registration_service.persistence.jpa.user;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@Entity
@Table(name = "users")
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
class UserJpaEntity {

    @Id
    private final String login;

    @EqualsAndHashCode.Exclude
    private String password;

    private String email;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private FIO fio;

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
    @Table(name = "users_fio")
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
