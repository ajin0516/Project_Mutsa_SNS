package com.finalproject_sns.controller;

import com.finalproject_sns.domain.Response;
import com.finalproject_sns.domain.dto.user.*;
import com.finalproject_sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest requestDto) {
        UserDto userDto = userService.join(requestDto);
        return Response.success(new UserJoinResponse(userDto.getId(),userDto.getUserName()));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest requestDto) {
        String token = userService.login(requestDto.getUserName(), requestDto.getPassword());
        return Response.success(new UserLoginResponse(token));
    }
}
