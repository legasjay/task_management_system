package com.olegandreevich.tms.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException(String email) {
        super("Не удалось найти пользователя с email: " + email);
    }
}
