package ru.bank_x.registration_service.services.verification;

public class VerificationRequestNotFoundException extends RuntimeException {

    private String login;

    public VerificationRequestNotFoundException(String login) {
        this.login = login;
    }

    @Override
    public String getMessage() {
        return String.format("Verification request for user with login [%s] not found in DB", login);
    }
}
