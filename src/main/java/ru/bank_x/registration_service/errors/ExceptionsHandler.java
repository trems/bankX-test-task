package ru.bank_x.registration_service.errors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.bank_x.registration_service.services.registration.RegistrationFormNotValidException;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@ControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RegistrationFormNotValidException.class)
    protected ResponseEntity<Object> handleRegistrationFormNotValid(RegistrationFormNotValidException ex) {
        final BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> fieldErrorMap = getFieldsErrorsMapping(bindingResult);
        List<String> globalErrors = getGlobalErrors(bindingResult);

        ApiErrorDetails errorDetails = new ApiErrorDetails(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), globalErrors, fieldErrorMap);
        return new ResponseEntity<>(errorDetails, errorDetails.getStatus());
    }

    private Map<String, String> getFieldsErrorsMapping(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .collect(toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage));
    }

    private List<String> getGlobalErrors(BindingResult bindingResult) {
        return bindingResult.getGlobalErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(toList());
    }
}
