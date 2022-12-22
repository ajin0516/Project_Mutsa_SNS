package com.finalproject_sns.controller;


import com.finalproject_sns.config.JwtTokenFilter;
import com.finalproject_sns.domain.Response;
import com.finalproject_sns.domain.dto.post.PostDto;
import com.finalproject_sns.domain.dto.post.PostRequest;
import com.finalproject_sns.domain.dto.post.PostResponse;
import com.finalproject_sns.domain.dto.user.UserDto;
import com.finalproject_sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<PostResponse> write(@RequestBody PostRequest requestDto, Authentication authentication) {
        String userName = authentication.getName();
        PostDto postDto = postService.create(requestDto, userName);
        return Response.success(new PostResponse("포스트 등록 완료", postDto.getId()));
    }
}
