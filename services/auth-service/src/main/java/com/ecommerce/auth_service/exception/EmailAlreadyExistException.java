package com.ecommerce.auth_service.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistException extends BaseException {
    public EmailAlreadyExistException(String email) {
        super("Email already have: " + email, HttpStatus.CONFLICT, "AUTH_001");
    }
}
