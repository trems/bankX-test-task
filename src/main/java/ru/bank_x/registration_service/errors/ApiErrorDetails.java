package ru.bank_x.registration_service.errors;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@Data
public class ApiErrorDetails {
    private HttpStatus status;
    private String message;
    private List<String> globalErrors;
    private Map<String, String> fieldErrors;

    public ApiErrorDetails(HttpStatus status, String message, List<String> globalErrors, Map<String, String> fieldErrors) {
        this.status = status;
        this.message = message;
        this.globalErrors = globalErrors;
        this.fieldErrors = fieldErrors;
    }
}
