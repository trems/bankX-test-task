package ru.bank_x.registration_service.persistence.jpa.verification;

import org.springframework.stereotype.Component;
import ru.bank_x.registration_service.messaging.dto.RegisterVerificationRequest;
import ru.bank_x.registration_service.services.verification.VerificationRequestConverter;

@Component
class VerificationRequestJpaEntityConverter implements VerificationRequestConverter<VerificationRequestJpaEntity> {

    @Override
    public VerificationRequestJpaEntity convert(RegisterVerificationRequest request) {
        return VerificationRequestJpaEntity.builder()
                .login(request.getLogin())
                .requestId(request.getRequestId())
                .build();
    }

    @Override
    public RegisterVerificationRequest convert(VerificationRequestJpaEntity entity) {
        return RegisterVerificationRequest.builder()
                .login(entity.getLogin())
                .requestId(entity.getRequestId())
                .build();
    }
}
