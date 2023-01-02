package com.finalproject_sns.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class ErrorResponse {

    private ErrorCode errorCode;
    private String message;
}
