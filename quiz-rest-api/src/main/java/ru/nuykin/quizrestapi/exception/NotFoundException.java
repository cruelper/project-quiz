package ru.nuykin.quizrestapi.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {
    public NotFoundException(String id) {
        super("Object with id=" + id + " not found", HttpStatus.NOT_FOUND);
    }
}
