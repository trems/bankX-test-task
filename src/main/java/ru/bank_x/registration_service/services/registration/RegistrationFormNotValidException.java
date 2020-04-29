package ru.bank_x.registration_service.services.registration;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class RegistrationFormNotValidException extends RuntimeException {

    private final BindingResult bindingResult;

    public RegistrationFormNotValidException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    @Override
    public String getMessage() {
        return String.format("Validation failed for registration form. Errors count: %d", bindingResult.getErrorCount());
    }
}
