package com.finalproject_sns.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalproject_sns.domain.dto.Response;
import com.finalproject_sns.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException {
        String exception = (String) request.getAttribute("exception");

        log.info("log: exception: {} ", exception);

        if (exception.equals(ErrorCode.INVALID_PERMISSION.name())) {
            setResponse(response, ErrorCode.INVALID_PERMISSION);
        } else if (exception.equals(ErrorCode.EXPIRED_TOKEN.name())) {
            setResponse(response, ErrorCode.EXPIRED_TOKEN);
        } else if (exception.equals(ErrorCode.INVALID_TOKEN.name())) {
            setResponse(response, ErrorCode.INVALID_TOKEN);
        } else {
            setResponse(response, ErrorCode.INVALID_PERMISSION);
        }
    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());

        response.getWriter().print(
                objectMapper.writeValueAsString(
                        Response.error(errorCode.name(), errorCode.getMessage()))
                );
    }
}

