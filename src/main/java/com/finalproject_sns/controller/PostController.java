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
        PostResponse postResponse = postService.create(postRequest, userName);
        return Response.success(postResponse);
    }

    @GetMapping("/{postId}")
    public Response<PostSearchResponse> getPost(@PathVariable Long postId) {
        PostSearchResponse postSearchResponse = postService.findById(postId);
        return Response.success(postSearchResponse);
    }
}
