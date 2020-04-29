package ru.bank_x.registration_service.messaging.dto;

import lombok.Value;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

/**
 * POJO, представляющий форму регистрации
 */
@Value
public class RegistrationForm {

    private static final String EMAIL_REGEX = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    @Length(min = 4, message = "Login must be at least 4 characters long")
    String login;

    @NotNull
    @Size(min = 6, message = "Password must be at least 6 characters long")
    String password;
    String confirmPassword;

    @Email(regexp = EMAIL_REGEX, message = "Email is invalid")
    String email;

    @NotBlank String surname;
    @NotBlank String name;
    @NotBlank String patronymic;

    @AssertTrue(message = "Passwords must be equal")
    boolean isPasswordConfirmed() {
        return password.equals(confirmPassword);
    }
}
