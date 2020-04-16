package ru.bank_x.registration_service.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bank_x.registration_service.domain.User;
import ru.bank_x.registration_service.data.UserRepository;
import ru.bank_x.registration_service.dto.RegistrationForm;
import ru.bank_x.registration_service.messaging.verification.RegisterVerificationService;
import ru.bank_x.registration_service.utils.PasswordEncoder;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class RegistrationController {

    private UserRepository userRepository;
    private RegisterVerificationService registerVerificationService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationController(UserRepository userRepository, RegisterVerificationService registerVerificationService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.registerVerificationService = registerVerificationService;
        this.passwordEncoder = passwordEncoder;
    }

    /*
     Пример валидного запроса:
     POST http://localhost:9000/register
     Content-Type: application/json
     {
     "login": "user",
     "password": "password",
     "confirmPassword": "password",
     "email": "email@domain.ru",
     "surname": "Ivanov",
     "name": "Ivan",
     "patronymic": "Ivanovich"
     }
     */
    @PostMapping
    public User processRegistration(@RequestBody @Valid RegistrationForm registrationForm, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining("; ", "Registration form errors: ", ""));
            log.error(errors);
            throw new ValidationException(errors);
        }
        User savedUser = userRepository.save(registrationForm.toUser(passwordEncoder));
        registerVerificationService.verifyUser(savedUser);
        return savedUser;
    }
}
