package ru.bank_x.registration_service.messaging;

import org.springframework.mail.SimpleMailMessage;

import java.util.concurrent.TimeoutException;

/**
 * Ориентировочный интерфейс мейлера.
 */
public interface SendMailer {

    void sendMail(SimpleMailMessage mailMessage) throws TimeoutException;
}
