package ru.bank_x.registration_service.services.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bank_x.registration_service.domain.User;
import ru.bank_x.registration_service.messaging.dto.RegistrationForm;
import ru.bank_x.registration_service.persistence.RegistrationFormDAO;
import ru.bank_x.registration_service.persistence.UserDAO;

@Service
class UserPersistenceServiceImpl implements UserPersistenceService {

    private RegistrationFormDAO registrationFormDAO;
    private UserDAO userDAO;

    @Autowired
    public UserPersistenceServiceImpl(RegistrationFormDAO registrationFormDAO, UserDAO userDAO) {
        this.registrationFormDAO = registrationFormDAO;
        this.userDAO = userDAO;
    }

    @Override
    public User persistUser(RegistrationForm form) {
        return registrationFormDAO.save(form);
    }

    @Override
    public User persistUser(User user) {
        return userDAO.save(user);
    }
}
