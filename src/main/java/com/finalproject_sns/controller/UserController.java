package com.finalproject_sns.controller;

import com.finalproject_sns.domain.Response;
import com.finalproject_sns.domain.dto.UserDto;
import com.finalproject_sns.domain.dto.UserJoinRequest;
import com.finalproject_sns.domain.dto.UserJoinResponse;
import com.finalproject_sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest requestDto) {
        UserDto userDto = userService.join(requestDto);
        return Response.success(new UserJoinResponse(userDto.getId(),userDto.getUserName()));
    }
}
