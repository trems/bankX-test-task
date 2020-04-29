package ru.bank_x.registration_service.persistence.jpa.verification;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface RegisterVerificationRequestRepository extends CrudRepository<VerificationRequestJpaEntity, String> {
}
