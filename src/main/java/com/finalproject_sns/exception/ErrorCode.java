package com.finalproject_sns.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "UserName이 중복됩니다."), // 409
    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "Not founded"),  // 404
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "패스워드가 잘못되었습니다."), // 401
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "사용자가 권한이 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 포스트가 없습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB에러"), // 500
    UNKNOWN_ERROR(HttpStatus.PROCESSING,"알 수 없는 에러입니다,"), // 102
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다");

    private HttpStatus httpStatus;
    private String message;
}
