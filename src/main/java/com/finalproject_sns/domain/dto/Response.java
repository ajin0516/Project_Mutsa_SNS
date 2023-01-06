package com.finalproject_sns.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Response<T> {

    private String resultCode;
    private T result;

    public static <T> Response<T> error(String resultCode, T result) {
        return new Response(resultCode, result);
    }

    public static <T> Response<T> success(T result) {
        return new Response("SUCCESS", result);
    }

    public static Response success(String result) {
        return new Response("SUCCESS", result);
    }

}
