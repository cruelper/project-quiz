package ru.nuykin.quizrestapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private Long timestamp;
    private HttpStatus status;

    public ApiException(String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }
}
