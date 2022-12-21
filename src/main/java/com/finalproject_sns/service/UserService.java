package com.finalproject_sns.service;

import com.finalproject_sns.domain.User;
import com.finalproject_sns.domain.dto.UserDto;
import com.finalproject_sns.domain.dto.UserJoinRequest;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.exception.UserAppException;
import com.finalproject_sns.repository.UserRepository;
import com.finalproject_sns.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}")
    private String secretKey;

    private long expireTimeMs = 1000 * 60 * 60L;

    public UserDto join(UserJoinRequest requestDto) {

        // 회원 중복 체크 -> 중복이면 Exception 발생
        userRepository.findByUserName(requestDto.getUserName())
                .ifPresent( user -> {
                    throw new UserAppException(ErrorCode.DUPLICATED_USER_NAME, requestDto.getUserName() + "은 중복입니다.");
                });

        // 회원가입
        User saveUser = userRepository.save(requestDto.toEntity(encoder.encode(requestDto.getPassword())));

        return  UserDto.builder()
                .id(saveUser.getId())
                .userName(saveUser.getUserName())
                .build();

    }

    public String login(String userName, String password) {
        // username 여부 확인
        User user = userRepository.findByUserName(userName).orElseThrow(
                ()-> new UserAppException(ErrorCode.USERNAME_NOT_FOUND,userName + "님은 가입한적이 없습니다")
        );

        // password 일치 여부 확인
        if(!encoder.matches(password,user.getPassword())){
            throw new UserAppException(ErrorCode.INVALID_PASSWORD, "password가 잘못 됐습니다");
        }

        // token 발행
        return JwtTokenUtil.createToken(userName, secretKey, expireTimeMs);
    }
}
