package com.finalproject_sns.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AppException extends RuntimeException{

    private ErrorCode errorCode;
    private String message;

    @Override
    public String toString() {
        if(message == null) return errorCode.getMessage();
        return String.format("%s, %s", errorCode.getMessage(), message);
    }
}
