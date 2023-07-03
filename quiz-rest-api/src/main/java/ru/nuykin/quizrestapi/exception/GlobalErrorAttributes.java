package ru.nuykin.quizrestapi.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {
    @Data
    @Builder
    public static class ErrorAttributes {
        private String message;
        private String path;
        private HttpStatus code;
    }
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = new HashMap<>();
        Throwable error = getError(request);

        errorAttributes.put("message", error.getMessage());
        errorAttributes.put("path", request.path());
        errorAttributes.put("code", error instanceof ApiException ? ((ApiException) error).getStatus() : HttpStatus.BAD_REQUEST);

        var a = error instanceof ApiException;

        return errorAttributes;
    }
}
