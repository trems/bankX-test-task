package ru.bank_x.registration_service.utils;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class SimplePasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));
    }
}
