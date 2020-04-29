package ru.bank_x.registration_service.persistence.jpa.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.bank_x.registration_service.messaging.dto.RegistrationForm;
import ru.bank_x.registration_service.services.registration.RegistrationFormConverter;

@Component
class RegistrationFormToUserJpaEntityConverter implements RegistrationFormConverter<UserJpaEntity> {

    private PasswordEncoder encoder;

    @Autowired
    public RegistrationFormToUserJpaEntityConverter(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public UserJpaEntity convert(RegistrationForm form) {
        return UserJpaEntity.builder()
                .login(form.getLogin())
                .password(encoder.encode(form.getPassword()))
                .email(form.getEmail())
                .fio(new UserJpaEntity.FIO(form.getSurname(), form.getName(), form.getPatronymic()))
                .build();
    }

    @Override
    public RegistrationForm convert(UserJpaEntity form) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
