package ru.bank_x.registration_service.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.bank_x.registration_service.dto.RegisterVerificationRequest;

@Repository
public interface RegisterVerificationRequestRepository extends CrudRepository<RegisterVerificationRequest, String> {
}
