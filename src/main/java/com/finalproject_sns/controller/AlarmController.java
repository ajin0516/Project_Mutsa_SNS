package com.finalproject_sns.controller;

import com.finalproject_sns.domain.Alarm;
import com.finalproject_sns.domain.dto.Response;
import com.finalproject_sns.domain.dto.alarm.AlarmResponse;
import com.finalproject_sns.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping
    public Response<Page<AlarmResponse>> alarmList(@PageableDefault(size = 20, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) {
//    public Response<Page<AlarmResponse>> alarmList(@PageableDefault(size = 20, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AlarmResponse> list = alarmService.findAlarmList(pageable, authentication.getName());
        return Response.success(list);
    }
}
