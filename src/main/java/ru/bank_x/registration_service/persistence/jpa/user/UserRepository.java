package ru.bank_x.registration_service.persistence.jpa.user;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
interface UserRepository extends CrudRepository<UserJpaEntity, String> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserJpaEntity u SET u.verified = :verified WHERE u.login = :login")
    void updateVerifiedByLogin(@Param("login") String login, @Param("verified") boolean verified);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserJpaEntity u SET u.notified = :notified WHERE u.login = :login")
    void updateNotifiedByLogin(@Param("login") String login, @Param("notified") boolean notified);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserJpaEntity u SET u.verified = :verified, u.notified = :notified WHERE u.login = :login")
    void updateVerifiedAndNotifiedByLogin(@Param("login") String login, @Param("verified") boolean verified, @Param("notified") boolean notified);

    Iterable<UserJpaEntity> findAllByNotifiedAndVerifiedIsNotNull(@Param("notified") boolean notified);
}
