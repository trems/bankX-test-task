package ru.bank_x.registration_service.dto;

import lombok.Value;
import org.hibernate.validator.constraints.Length;
import ru.bank_x.registration_service.domain.User;
import ru.bank_x.registration_service.utils.PasswordEncoder;

import javax.validation.constraints.*;

/**
 * POJO, представляющий форму регистрации
 */
@Value
public class RegistrationFormDto {

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

    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(login, passwordEncoder.encode(password), email, new User.FIO(surname, name, patronymic));
    }

    @AssertTrue(message = "Passwords must be equal")
    boolean isPasswordConfirmed() {
        return password.equals(confirmPassword);
    }
}
