package ru.bank_x.registration_service.data;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.bank_x.registration_service.domain.User;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.verified = :verified WHERE u.login = :login")
    void updateVerifiedByLogin(@Param("login") String login, @Param("verified") boolean verified);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.notified = :notified WHERE u.login = :login")
    void updateNotifiedByLogin(@Param("login") String login, @Param("notified") boolean notified);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.verified = :verified, u.notified = :notified WHERE u.login = :login")
    void updateVerifiedAndNotifiedByLogin(@Param("login") String login, @Param("verified") boolean verified, @Param("notified") boolean notified);

    Iterable<User> findAllByNotifiedAndVerifiedIsNotNull(@Param("notified") boolean notified);
}
