package com.finalproject_sns.controller;

import com.finalproject_sns.domain.dto.Response;
import com.finalproject_sns.domain.dto.user.*;
import com.finalproject_sns.service.UserService;
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

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest requestDto) {
        UserDto userDto = userService.join(requestDto);
        return Response.success(new UserJoinResponse(userDto.getId(), userDto.getUserName()));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest requestDto) {
        String token = userService.login(requestDto.getUserName(), requestDto.getPassword());
        return Response.success(new UserLoginResponse(token));
    }

    @PostMapping("{id}/role/change")
    public Response<UserRoleResponse> changeRole(@PathVariable Long id, Authentication authentication) {
        UserRoleResponse userRoleResponse = userService.changeRole(id, authentication.getName());
        return Response.success(userRoleResponse);
    }
}
