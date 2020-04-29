package ru.bank_x.registration_service.domain;

import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String login;
    private String email;
    private FIO fio;
    private Date registeredAt;

    private boolean verified;
    private boolean notified;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
    public static class FIO {

        private final String surname;
        private final String name;
        private final String patronymic;

        @Override
        public String toString() {
            return String.join(" ", surname, name, patronymic);
        }

    }
}
