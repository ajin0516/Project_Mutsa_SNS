package com.finalproject_sns.controller;

import com.finalproject_sns.domain.dto.Response;
import com.finalproject_sns.domain.dto.user.*;
import com.finalproject_sns.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @ApiOperation(value = "회원가입", notes = "password 암호화하여 DB 저장")
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest requestDto) {
        UserDto userDto = userService.join(requestDto);
        return Response.success(new UserJoinResponse(userDto.getId(), userDto.getUserName()));
    }

    @ApiOperation(value = "로그인", notes = "로그인 성공 시 JWT 토큰 발행")
    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest requestDto) {
        String token = userService.login(requestDto.getUserName(), requestDto.getPassword());
        return Response.success(new UserLoginResponse(token));
    }

    @ApiOperation(value = "회원 권한 변경", notes = "관리자만 가능")
    @PostMapping("{id}/role/change")
    public Response<UserRoleResponse> changeRole(@PathVariable Long id, Authentication authentication) {
        UserRoleResponse userRoleResponse = userService.changeRole(id, authentication.getName());
        return Response.success(userRoleResponse);
    }
}
