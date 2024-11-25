package com.olegandreevich.tms.util;

import com.olegandreevich.tms.util.exceptions.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFound(EntityNotFoundException ex) {
        return new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleTeamNotFound(ResourceNotFoundException e, Model model) {
        return new ApiError(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        return new ApiError(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ApiError usernameNotFound(UsernameNotFoundException e, Model model) {
        return new ApiError(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolation(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            sb.append(violation.getPropertyPath()).append(": ")
                    .append(violation.getMessage()).append("\n");
        }
        return new ApiError(HttpStatus.BAD_REQUEST.value(), sb.toString());
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        return new ApiError(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiError handleInvalidPasswordException(InvalidPasswordException ex) {
        return new ApiError(HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage());
    }
}


