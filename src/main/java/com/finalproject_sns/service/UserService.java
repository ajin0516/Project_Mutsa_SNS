package com.finalproject_sns.service;

import com.finalproject_sns.domain.User;
import com.finalproject_sns.domain.dto.UserDto;
import com.finalproject_sns.domain.dto.UserJoinRequest;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.exception.UserAppException;
import com.finalproject_sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto join(UserJoinRequest requestDto) {

        // 회원 중복 체크 -> 중복이면 Exception 발생
        userRepository.findByUserName(requestDto.getUserName())
                .ifPresent( user -> {
                    throw new UserAppException(ErrorCode.DUPLICATED_USER_NAME, requestDto.getUserName() + "은 중복입니다.");
                });

        // 회원가입
        User saveUser = userRepository.save(requestDto.toEntity());

        return  UserDto.builder()
                .id(saveUser.getId())
                .userName(saveUser.getUserName())
                .build();

    }
}
