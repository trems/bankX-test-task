package ru.bank_x.registration_service.messaging.dto;

import lombok.Value;

@Value
public class RegisterVerificationResponse implements Response {

    RegisterVerificationRequest request;
    boolean verified;
}
