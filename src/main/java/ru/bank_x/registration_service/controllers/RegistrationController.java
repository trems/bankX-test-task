package ru.bank_x.registration_service.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bank_x.registration_service.domain.User;
import ru.bank_x.registration_service.messaging.dto.RegistrationForm;
import ru.bank_x.registration_service.services.registration.RegistrationFormNotValidException;
import ru.bank_x.registration_service.services.registration.UserRegistrationServiceFacade;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class RegistrationController {

    private UserRegistrationServiceFacade userRegistrationService;

    @Autowired
    public RegistrationController(UserRegistrationServiceFacade userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
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
    public User processRegistration(@RequestBody @Valid RegistrationForm registrationForm, BindingResult bindingResult) throws RegistrationFormNotValidException {
        if (bindingResult.hasErrors()) {
            throw new RegistrationFormNotValidException(bindingResult);
        }
        return userRegistrationService.registerUser(registrationForm);
    }
}
