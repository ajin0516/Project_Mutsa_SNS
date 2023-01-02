package com.finalproject_sns.domain.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class UserRoleResponse {

    private final String message;
    private final String userName;
}
