package ru.bank_x.registration_service.persistence.jpa.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bank_x.registration_service.domain.User;
import ru.bank_x.registration_service.messaging.dto.RegistrationForm;
import ru.bank_x.registration_service.services.registration.RegistrationFormConverter;

import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Component
class UserJpaEntityConverter {

    private RegistrationFormConverter<UserJpaEntity> registrationFormConverter;

    @Autowired
    public UserJpaEntityConverter(RegistrationFormConverter<UserJpaEntity> registrationFormConverter) {
        this.registrationFormConverter = registrationFormConverter;
    }

    protected List<User> transformEntitiesToUsersList(Iterable<UserJpaEntity> allUsers) {
        return StreamSupport.stream(allUsers.spliterator(), false)
                .map(this::getUser)
                .collect(toList());
    }

    protected List<UserJpaEntity> transformUsersListToEntities(List<User> users) {
        return users.stream()
                .map(this::getUserJpaEntity)
                .collect(toList());
    }

    protected User getUser(UserJpaEntity userEntity) {
        UserJpaEntity.FIO fio = userEntity.getFio();
        return User.builder()
                .login(userEntity.getLogin())
                .email(userEntity.getEmail())
                .fio(new User.FIO(fio.getSurname(), fio.getName(), fio.getPatronymic()))
                .registeredAt(userEntity.getRegisteredAt())
                .verified(userEntity.isVerified())
                .notified(userEntity.isNotified())
                .build();
    }

    protected UserJpaEntity getUserJpaEntity(RegistrationForm form) {
        return registrationFormConverter.convert(form);
    }

    protected UserJpaEntity getUserJpaEntity(User user) {
        User.FIO fio = user.getFio();
        return UserJpaEntity.builder()
                .login(user.getLogin())
                .email(user.getEmail())
                .fio(new UserJpaEntity.FIO(fio.getSurname(), fio.getName(), fio.getPatronymic()))
                .registeredAt(user.getRegisteredAt())
                .verified(user.isVerified())
                .notified(user.isNotified())
                .build();
    }

}
