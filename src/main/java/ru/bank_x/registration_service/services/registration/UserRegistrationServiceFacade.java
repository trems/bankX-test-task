package ru.bank_x.registration_service.services.registration;

import ru.bank_x.registration_service.domain.User;
import ru.bank_x.registration_service.messaging.dto.RegistrationForm;

/**
 * является точкой входа в Application Layer
 */
public interface UserRegistrationServiceFacade {

    User registerUser(RegistrationForm registrationForm);
}
