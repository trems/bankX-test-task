package ru.bank_x.registration_service.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.bank_x.registration_service.domain.User;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {

    private UserRepository userRepository;
    private EntityManager entityManager;
    private User user = new User("user", "password", "user@domain.ru",
            new User.FIO("Ivanov","Ivan", "Ivanovich"));

    @Autowired
    UserRepositoryTest(UserRepository userRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        entityManager.clear();
        userRepository.save(user);
    }

    @Test
    void updateVerifiedByLogin() {
        userRepository.updateVerifiedByLogin(user.getLogin(), true);
        User userFromDB = userRepository.findById(this.user.getLogin()).get();
        Assertions.assertTrue(userFromDB.isVerified());
    }

    @Test
    void updateNotifiedByLogin() {
        userRepository.updateNotifiedByLogin(user.getLogin(), true);
        User userFromDB = userRepository.findById(this.user.getLogin()).get();
        Assertions.assertTrue(userFromDB.isNotified());
    }

    @Test
    void updateVerifiedAndNotifiedByLogin() {
        userRepository.updateVerifiedAndNotifiedByLogin(user.getLogin(), true, true);
        User userFromDB = userRepository.findById(this.user.getLogin()).get();
        Assertions.assertTrue(userFromDB.isVerified());
        Assertions.assertTrue(userFromDB.isNotified());
    }

    @Test
    void findAllByNotifiedAndVerifiedIsNotNull() {
        userRepository.updateVerifiedAndNotifiedByLogin(user.getLogin(), true, false);
        Iterable<User> notifiedTrue = userRepository.findAllByNotifiedAndVerifiedIsNotNull(true);
        assertThat(notifiedTrue).isEmpty();

        Iterable<User> notifiedFalse = userRepository.findAllByNotifiedAndVerifiedIsNotNull(false);
        assertThat(notifiedFalse).hasSize(1);
    }
}