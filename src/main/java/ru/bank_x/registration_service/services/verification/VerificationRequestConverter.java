package ru.bank_x.registration_service.services.verification;

import ru.bank_x.registration_service.messaging.dto.RegisterVerificationRequest;

public interface VerificationRequestConverter<ToType> {

    ToType convert(RegisterVerificationRequest request);

    RegisterVerificationRequest convert(ToType request);
}
