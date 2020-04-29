package ru.bank_x.registration_service.persistence.jpa.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bank_x.registration_service.domain.User;
import ru.bank_x.registration_service.persistence.UserDAO;
import ru.bank_x.registration_service.services.persistence.UserNotFoundException;

import java.util.List;
import java.util.Optional;

@Component
class UserJpaDAO implements UserDAO {

    private UserRepository userRepository;
    private UserJpaEntityConverter converter;

    @Autowired
    public UserJpaDAO(UserRepository userRepository, UserJpaEntityConverter converter) {
        this.userRepository = userRepository;
        this.converter = converter;
    }

    @Override
    public User save(User user) {
        UserJpaEntity savedUser = userRepository.save(converter.getUserJpaEntity(user));
        return converter.getUser(savedUser);
    }

    @Override
    public List<User> saveAll(List<User> users) {
        Iterable<UserJpaEntity> jpaEntities = userRepository.saveAll(converter.transformUsersListToEntities(users));
        return converter.transformEntitiesToUsersList(jpaEntities);
    }

    @Override
    public User findByLogin(String login) {
        Optional<UserJpaEntity> userEntityOptional = userRepository.findById(login);
        UserJpaEntity userEntity = userEntityOptional.orElseThrow(() -> new UserNotFoundException(login));
        return converter.getUser(userEntity);
    }

    @Override
    public List<User> findAll() {
        Iterable<UserJpaEntity> allUsers = userRepository.findAll();
        return converter.transformEntitiesToUsersList(allUsers);
    }

    @Override
    public Iterable<User> findAllByNotifiedAndVerifiedIsNotNull(boolean notified) {
        Iterable<UserJpaEntity> users = userRepository.findAllByNotifiedAndVerifiedIsNotNull(notified);
        return converter.transformEntitiesToUsersList(users);
    }

    @Override
    public void updateVerifiedByLogin(String login, boolean verified) {
        userRepository.updateVerifiedByLogin(login, verified);
    }

    @Override
    public void updateNotifiedByLogin(String login, boolean notified) {
        userRepository.updateNotifiedByLogin(login, notified);
    }

    @Override
    public void updateVerifiedAndNotifiedByLogin(String login, boolean verified, boolean notified) {
        userRepository.updateVerifiedAndNotifiedByLogin(login, verified, notified);
    }

    @Override
    public void deleteByLogin(String login) {
        userRepository.deleteById(login);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(converter.getUserJpaEntity(user));
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }


}
