package com.finalproject_sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalproject_sns.domain.AlarmType;
import com.finalproject_sns.domain.dto.alarm.AlarmResponse;
import com.finalproject_sns.exception.AppException;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.service.AlarmService;
import com.finalproject_sns.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlarmController.class)
class AlarmControllerTest {

    @MockBean
    AlarmService alarmService;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    @DisplayName("알람 목록 조회 성공")
    @WithMockUser
    void alarm_success_list() throws Exception {


        AlarmResponse alarmResponse = AlarmResponse.builder()
                .id(1L)
                .alarmType(AlarmType.NEW_LIKE_ON_POST)
                .targetId(2L)
                .fromUserId(2L)
                .text(AlarmType.NEW_LIKE_ON_POST.getMessage())
                .createdAt(LocalDateTime.now())
                .build();
//        Page<AlarmResponse> responsePages = new PageImpl<>(AlarmResponse.class);

        when(alarmService.findAlarmList(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/alarms")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(alarmResponse)))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("알람 목록 조회 실패 - 로그인 X")
    @WithAnonymousUser
    void alarm_fail_not_login() throws Exception {

        when(alarmService.findAlarmList(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, "사용자 권한이 없습니다."));

        mockMvc.perform(get("/api/v1/alarms")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}