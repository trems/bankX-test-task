package ru.bank_x.registration_service.persistence;

import ru.bank_x.registration_service.domain.User;

import java.util.List;

public interface UserDAO {

    User save(User user);

    List<User> saveAll(List<User> users);

    User findByLogin(String login);

    List<User> findAll();

    Iterable<User> findAllByNotifiedAndVerifiedIsNotNull(boolean notified);

    void updateVerifiedByLogin(String login, boolean verified);

    void updateNotifiedByLogin(String login, boolean notified);

    void updateVerifiedAndNotifiedByLogin(String login, boolean verified, boolean notified);

    void deleteByLogin(String login);

    void delete(User user);

    void deleteAll();
}
