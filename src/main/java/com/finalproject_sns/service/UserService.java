package com.finalproject_sns.service;

import com.finalproject_sns.domain.User;
import com.finalproject_sns.domain.UserRole;
import com.finalproject_sns.domain.dto.user.UserDto;
import com.finalproject_sns.domain.dto.user.UserJoinRequest;
import com.finalproject_sns.domain.dto.user.UserRoleRequest;
import com.finalproject_sns.domain.dto.user.UserRoleResponse;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.exception.AppException;
import com.finalproject_sns.repository.UserRepository;
import com.finalproject_sns.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}")
    private String secretKey;

    private long expireTimeMs = 1000 * 60 * 180L;

    @Transactional
    public UserDto join(UserJoinRequest requestDto) {
        // 회원 중복 체크 -> 중복이면 Exception 발생
        userRepository.findByUserName(requestDto.getUserName())
                .ifPresent( user -> {
                    throw new AppException(ErrorCode.DUPLICATED_USER_NAME, requestDto.getUserName() + "은 중복입니다.");
                });

        // 회원가입
        User saveUser = userRepository.save(requestDto.toEntity(encoder.encode(requestDto.getPassword())));

        return  UserDto.builder()
                .id(saveUser.getId())
                .userName(saveUser.getUserName())
                .build();

    }

    @Transactional
    public String login(String userName, String password) {
        // username 여부 확인
        User user = userRepository.findByUserName(userName).orElseThrow(
                ()-> new AppException(ErrorCode.USERNAME_NOT_FOUND,userName + "님은 가입한적이 없습니다")
        );

        // password 일치 여부 확인
        if(!encoder.matches(password,user.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD, "password가 잘못 됐습니다");
        }

        // token 발행
        return JwtTokenUtil.createToken(userName, secretKey, expireTimeMs);
    }

    public User getUserByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_PERMISSION, "사용자 권한이 없습니다"));
    }

    public UserRoleResponse changeRole(Long id, String userName, UserRoleRequest userRoleRequest) {
        // 토큰을 통해 인증 된 회원 중 일치하는 name 없음
        User admin = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "존재하지 않는 회원입니다."));

        // id로 조회 시 존재 하지 않는 유저
        User changeUser = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "존재하지 않는 회원입니다."));

        log.info("changeUser.getRole()={}",changeUser.getRole());
        log.info("admin.getRole()={}",admin.getRole());
        if(changeUser.getRole().equals(UserRole.ADMIN)){
            throw new AppException(ErrorCode.INVALID_PERMISSION, changeUser.getUserName() + "님은 이미 관리자권한입니다.");
        }

        if (admin.getRole().equals(UserRole.ADMIN)) {
            changeUser.upgradeAdmin(UserRole.ADMIN);
        } else {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "사용자 권한이 없습니다");
        }

        userRepository.save(userRoleRequest.toEntity(changeUser));

        return UserRoleResponse.builder()
                .message("관리자로 권한이 변경되었습니다.")
                .userName(changeUser.getUserName())
                .build();
    }
}
