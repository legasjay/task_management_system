package com.olegandreevich.tms.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String email) {
        super("Username уже существует: " + email);
    }
}