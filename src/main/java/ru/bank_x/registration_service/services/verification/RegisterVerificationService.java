package ru.bank_x.registration_service.services.verification;

import ru.bank_x.registration_service.domain.User;

public interface RegisterVerificationService {

    void verifyUser(User user);
}
