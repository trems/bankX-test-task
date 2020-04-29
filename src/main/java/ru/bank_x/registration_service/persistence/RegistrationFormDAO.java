package ru.bank_x.registration_service.persistence;

import ru.bank_x.registration_service.domain.User;
import ru.bank_x.registration_service.messaging.dto.RegistrationForm;

import java.util.List;

public interface RegistrationFormDAO {

    User save(RegistrationForm form);

    List<User> saveAll(List<RegistrationForm> forms);
}
