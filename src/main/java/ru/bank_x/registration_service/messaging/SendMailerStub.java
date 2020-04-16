package ru.bank_x.registration_service.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.bank_x.registration_service.utils.RandomBehaviorUtils;

import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class SendMailerStub implements SendMailer {

    private RandomBehaviorUtils randomBehaviorUtils;

    @Autowired
    public SendMailerStub(RandomBehaviorUtils randomBehaviorUtils) {
        this.randomBehaviorUtils = randomBehaviorUtils;
    }

    @Override
    public void sendMail(SimpleMailMessage mailMessage) throws TimeoutException {
        randomBehaviorUtils.simulateRandomSystemFailure();

        // ok.
        log.info("Email sent to: {}, body: {}",
                StringUtils.arrayToCommaDelimitedString(mailMessage.getTo()),
                mailMessage.getText());
    }
}
