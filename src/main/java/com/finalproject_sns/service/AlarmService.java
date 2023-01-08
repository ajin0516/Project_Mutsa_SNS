package com.finalproject_sns.service;

import com.finalproject_sns.domain.Alarm;
import com.finalproject_sns.domain.dto.alarm.AlarmResponse;
import com.finalproject_sns.repository.AlarmRepository;
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

    @Transactional(readOnly = true)
    public Page<AlarmResponse> findList(Pageable pageable) {
//        Optional<Alarm> alarmUser = alarmRepository.findByUser
//        Page<Alarm> alarmList = alarmRepository.findByDeletedAtNull( pageable);
        Page<Alarm> alarmList = alarmRepository.findByDeletedAtNull( pageable);
        Page<AlarmResponse> alarmResponses = AlarmResponse.toDtoList(alarmList);
        return alarmResponses;
    }
}
