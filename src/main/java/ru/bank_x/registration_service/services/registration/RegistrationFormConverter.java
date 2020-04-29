package ru.bank_x.registration_service.services.registration;

import ru.bank_x.registration_service.messaging.dto.RegistrationForm;

public interface RegistrationFormConverter<ToType> {

    ToType convert(RegistrationForm form);

    RegistrationForm convert(ToType form);
}
