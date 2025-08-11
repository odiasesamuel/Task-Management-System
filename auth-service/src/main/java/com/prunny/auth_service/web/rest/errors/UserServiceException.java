package com.prunny.auth_service.web.rest.errors;

public class UserServiceException extends RuntimeException {
    public UserServiceException(String message) {
        super(message);
    }
}
