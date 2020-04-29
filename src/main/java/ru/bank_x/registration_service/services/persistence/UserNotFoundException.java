package ru.bank_x.registration_service.services.persistence;

public class UserNotFoundException extends RuntimeException {

    private String login;

    public UserNotFoundException(String login) {
        this.login = login;
    }

    @Override
    public String getMessage() {
        return String.format("User with login [%s] not found in DB", login);
    }
}
