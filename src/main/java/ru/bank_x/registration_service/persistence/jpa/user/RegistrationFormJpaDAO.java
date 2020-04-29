package ru.bank_x.registration_service.persistence.jpa.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bank_x.registration_service.domain.User;
import ru.bank_x.registration_service.messaging.dto.RegistrationForm;
import ru.bank_x.registration_service.persistence.RegistrationFormDAO;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
class RegistrationFormJpaDAO implements RegistrationFormDAO {

    private UserRepository userRepository;
    private UserJpaEntityConverter converter;

    @Autowired
    public RegistrationFormJpaDAO(UserRepository userRepository, UserJpaEntityConverter converter) {
        this.userRepository = userRepository;
        this.converter = converter;
    }

    @Override
    public User save(RegistrationForm form) {
        UserJpaEntity savedUser = userRepository.save(converter.getUserJpaEntity(form));
        return converter.getUser(savedUser);
    }

    @Override
    public List<User> saveAll(List<RegistrationForm> forms) {
        List<UserJpaEntity> usersToSave = forms.stream()
                .map(converter::getUserJpaEntity)
                .collect(toList());
        Iterable<UserJpaEntity> savedUsers = userRepository.saveAll(usersToSave);
        return converter.transformEntitiesToUsersList(savedUsers);

    }
}
