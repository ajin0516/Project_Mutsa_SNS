package com.finalproject_sns.controller;

import com.finalproject_sns.domain.Response;
import com.finalproject_sns.domain.dto.post.*;
import com.finalproject_sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<PostResponse> write(@RequestBody PostRequest postRequest, Authentication authentication) {
        String userName = authentication.getName();
        PostRequest postDto = postService.create(postRequest, userName);
        return Response.success(new PostResponse("포스트 등록 완료", postDto.getId()));
    }

    @GetMapping("/{postId}")
    public Response<PostSearchRequest> getPost(@PathVariable Long postId) {
        PostSearchRequest postSearchRequest = postService.findById(postId);
        return Response.success(postSearchRequest);
    }
}
