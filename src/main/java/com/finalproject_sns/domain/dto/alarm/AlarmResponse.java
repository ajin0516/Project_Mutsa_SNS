package com.finalproject_sns.domain.dto.alarm;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.finalproject_sns.domain.Alarm;
import com.finalproject_sns.domain.AlarmType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
public class AlarmResponse {

    private Long id;
    private AlarmType alarmType;
    private Long fromUserId;
    private Long targetId;
    private String text;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;


    @Builder
    public AlarmResponse(Long id, AlarmType alarmType, Long fromUserId, Long targetId, String text, LocalDateTime createdAt) {
        this.id = id;
        this.alarmType = alarmType;
        this.fromUserId = fromUserId;
        this.targetId = targetId;
        this.text = text;
        this.createdAt = createdAt;
    }

    public static Page<AlarmResponse> toDtoList(Page<Alarm> alarmList) {
        Page<AlarmResponse> alarmDtoList = alarmList.map(a -> AlarmResponse.builder()
                .id(a.getId())
                .alarmType(a.getAlarmType())
                .fromUserId(a.getFromUserId())
                .targetId(a.getTargetId())
                .text(a.getText())
                .createdAt(a.getCreateAt())
                .build());
        return alarmDtoList;
    }
}
