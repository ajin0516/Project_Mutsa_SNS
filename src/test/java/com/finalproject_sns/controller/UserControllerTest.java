package com.finalproject_sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalproject_sns.domain.dto.user.UserDto;
import com.finalproject_sns.domain.dto.user.UserJoinRequest;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.exception.AppException;
import com.finalproject_sns.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    UserService userService;

    @MockBean
    BCryptPasswordEncoder encoder;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    @WithMockUser
    void join_success() throws Exception {
        UserJoinRequest userJoinRequest = new UserJoinRequest("test", "123");

        when(userService.join(any())).thenReturn(UserDto.builder()
                        .id(1L)
                .userName(userJoinRequest.getUserName())
                .build());

        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.userId").value("1"))
                .andExpect(jsonPath("$.result.userName").value("test"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패")
    @WithMockUser
    void join_fail() throws Exception {

        String password = "1234";
        UserJoinRequest userJoinRequest =UserJoinRequest.builder()
                .userName("test")
                .password(password)
                .build();

        when(userService.join(any())).thenThrow(new AppException(ErrorCode.DUPLICATED_USER_NAME, userJoinRequest.getUserName() + "은 중복된 이름입니다."));

        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isConflict());
    }


    @Test
    @DisplayName("로그인 실패 - id 없음")
    @WithMockUser
    void login_fail1() throws Exception {

        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("ajin")
                .password("1234")
                .build();

        when(userService.login(any(), any())).thenThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND,userJoinRequest.getUserName() + "은 존재하지 않는 ID입니다"));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("로그인 실패 - password 불일치")
    @WithMockUser
    void login_fail2() throws Exception {
        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("ajin")
                .password("1234")
                .build();

        when(userService.login(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PASSWORD, "password가 일치하지 않습니다."));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("로그인 성공")
    @WithMockUser
    void login_success() throws Exception {
        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("ajin")
                .password("1234")
                .build();

        when(userService.login(any(), any())).thenReturn("token");

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.result.jwt").exists());
    }
}