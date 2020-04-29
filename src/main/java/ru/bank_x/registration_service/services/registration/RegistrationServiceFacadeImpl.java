package ru.bank_x.registration_service.services.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bank_x.registration_service.domain.User;
import ru.bank_x.registration_service.messaging.dto.RegistrationForm;
import ru.bank_x.registration_service.services.persistence.UserPersistenceService;
import ru.bank_x.registration_service.services.verification.RegisterVerificationService;

@Component
class RegistrationServiceFacadeImpl implements UserRegistrationServiceFacade {

    private UserPersistenceService userPersistenceService;
    private RegisterVerificationService verificationService;

    @Autowired
    public RegistrationServiceFacadeImpl(UserPersistenceService userPersistenceService, RegisterVerificationService verificationService) {
        this.userPersistenceService = userPersistenceService;
        this.verificationService = verificationService;
    }

    @Override
    public User registerUser(RegistrationForm registrationForm) {
        User savedUser = userPersistenceService.persistUser(registrationForm);
        verificationService.verifyUser(savedUser);
        return savedUser;
    }
}
