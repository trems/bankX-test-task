package ru.bank_x.registration_service.services.registration;

import org.springframework.stereotype.Component;
import ru.bank_x.registration_service.domain.User;
import ru.bank_x.registration_service.messaging.dto.RegistrationForm;

@Component
class RegistrationFormToUserConverter implements RegistrationFormConverter<User> {

    @Override
    public User convert(RegistrationForm form) {
        User.FIO fio = new User.FIO(form.getSurname(), form.getName(), form.getPatronymic());
        return User.builder()
                .login(form.getLogin())
                .email(form.getEmail())
                .fio(fio)
                .build();
    }

    @Override
    public RegistrationForm convert(User form) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
