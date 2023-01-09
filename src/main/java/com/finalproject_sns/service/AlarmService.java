package com.finalproject_sns.service;

import com.finalproject_sns.domain.Alarm;
import com.finalproject_sns.domain.User;
import com.finalproject_sns.domain.dto.alarm.AlarmResponse;
import com.finalproject_sns.exception.AppException;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.repository.AlarmRepository;
import com.finalproject_sns.repository.PostRepository;
import com.finalproject_sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<AlarmResponse> findAlarmList(Pageable pageable, String userName) {
//        Optional<Alarm> alarmUser = alarmRepository.findByUser
//        Page<Alarm> alarmList = alarmRepository.findByDeletedAtNull( pageable);
        User user = userRepository.findByUserName(userName).orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "존재하지 앟는 유저입니다."));
//        Page<Alarm> alarmList = alarmRepository.findByDeletedAtNull(pageable);
        Page<Alarm> alarmList = alarmRepository.findByUserAndDeletedAtNull(user, pageable);
        Page<AlarmResponse> alarmResponses = AlarmResponse.toDtoList(alarmList);
        return alarmResponses;
    }
}
