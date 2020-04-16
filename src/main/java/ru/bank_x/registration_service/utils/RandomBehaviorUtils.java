package ru.bank_x.registration_service.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class RandomBehaviorUtils {

    public void simulateRandomSystemFailure() throws TimeoutException {
        if (shouldThrowTimeout()) {
            sleep();
            throw new TimeoutException("Timeout!");
        }

        if (shouldSleep()) {
            sleep();
        }
    }

    @SneakyThrows
    public void sleep() {
        Thread.sleep(TimeUnit.MINUTES.toMillis(1));
    }

    public boolean shouldSleep() {
        boolean should = new Random().nextInt(10) == 1;
        if (should) {
            log.warn("System failure. Go to sleep for 1 minute");
        }
        return should;
    }

    public boolean shouldThrowTimeout() {
        boolean should = new Random().nextInt(10) == 1;
        if (should) {
            log.warn("Should throw TimeoutException. Waiting for 1 minute");
        }
        return should;
    }

    public boolean isRegisterRequestApproved() {
        return new Random().nextBoolean();
    }
}
