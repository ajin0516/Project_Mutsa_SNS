package com.finalproject_sns.domain.dto;

import com.finalproject_sns.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserJoinRequest {

    private String userName;
    private String password;

    public User toEntity() {
        return User.builder()
                .userName(this.userName)
                .password(this.password)
                .build();
    }
}
