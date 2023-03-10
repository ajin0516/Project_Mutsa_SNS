package com.finalproject_sns.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserJoinResponse {

    private Long userId;
    private String userName;

}
