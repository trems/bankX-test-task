package ru.bank_x.registration_service.persistence.jpa.verification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bank_x.registration_service.messaging.dto.RegisterVerificationRequest;
import ru.bank_x.registration_service.persistence.VerificationRequestDAO;
import ru.bank_x.registration_service.services.verification.VerificationRequestConverter;
import ru.bank_x.registration_service.services.verification.VerificationRequestNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
class VerificationRequestJpaDAO implements VerificationRequestDAO {

    private RegisterVerificationRequestRepository requestRepository;
    private VerificationRequestConverter<VerificationRequestJpaEntity> converter;

    @Autowired
    VerificationRequestJpaDAO(RegisterVerificationRequestRepository requestRepository, VerificationRequestConverter<VerificationRequestJpaEntity> converter) {
        this.requestRepository = requestRepository;
        this.converter = converter;
    }

    @Override
    public RegisterVerificationRequest save(RegisterVerificationRequest request) {
        VerificationRequestJpaEntity entity = converter.convert(request);
        VerificationRequestJpaEntity savedRequest = requestRepository.save(entity);
        return converter.convert(savedRequest);
    }

    @Override
    public List<RegisterVerificationRequest> saveAll(List<RegisterVerificationRequest> requests) {
        List<VerificationRequestJpaEntity> entities = requests.stream()
                .map(converter::convert)
                .collect(Collectors.toList());

        Iterable<VerificationRequestJpaEntity> savedEntities = requestRepository.saveAll(entities);

        List<RegisterVerificationRequest> savedRequests = new ArrayList<>();
        savedEntities.forEach(entity -> savedRequests.add(converter.convert(entity)));
        return savedRequests;
    }

    @Override
    public RegisterVerificationRequest findById(String userLogin) {
        VerificationRequestJpaEntity entity = requestRepository.findById(userLogin).orElseThrow(() -> new VerificationRequestNotFoundException(userLogin));
        return converter.convert(entity);
    }

    @Override
    public List<RegisterVerificationRequest> findAll() {
        Iterable<VerificationRequestJpaEntity> allEntities = requestRepository.findAll();

        List<RegisterVerificationRequest> allRequests = new ArrayList<>();
        allEntities.forEach(entity -> allRequests.add(converter.convert(entity)));
        return allRequests;
    }

    @Override
    public void deleteById(String id) {
        requestRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        requestRepository.deleteAll();
    }
}
