package com.finalproject_sns.exception;

import com.finalproject_sns.domain.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionManager {

    @ExceptionHandler(UserAppException.class)
    public ResponseEntity<?> userAppExceptionHandler(UserAppException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(Response.error(e.getErrorCode().name(),e.getErrorCode().getMessage()));
    }
}
