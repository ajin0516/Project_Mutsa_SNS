package com.finalproject_sns.domain.dto.user;


import com.finalproject_sns.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleRequest {

    private String role;

    public User toEntity(User user) {
        return User.builder()
                .role(user.getRole())
                .build();
    }
}
