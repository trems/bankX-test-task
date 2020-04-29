package ru.bank_x.registration_service.services.persistence;

import ru.bank_x.registration_service.domain.User;
import ru.bank_x.registration_service.messaging.dto.RegistrationForm;

public interface UserPersistenceService {

    User persistUser(RegistrationForm form);

    User persistUser(User user);
}
