package ru.bank_x.registration_service.persistence;

import ru.bank_x.registration_service.messaging.dto.RegisterVerificationRequest;

import java.util.List;

public interface VerificationRequestDAO {

    RegisterVerificationRequest save(RegisterVerificationRequest request);

    List<RegisterVerificationRequest> saveAll(List<RegisterVerificationRequest> requests);

    RegisterVerificationRequest findById(String id);

    List<RegisterVerificationRequest> findAll();

    void deleteById(String id);

    void deleteAll();
}
