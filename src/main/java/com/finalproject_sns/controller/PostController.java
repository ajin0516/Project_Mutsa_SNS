package com.finalproject_sns.controller;

import com.finalproject_sns.domain.dto.Response;
import com.finalproject_sns.domain.dto.post.*;
import com.finalproject_sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public Response<PostSearchResponse> findOnePost(@PathVariable Long postId) {
        PostSearchResponse postSearchResponse = postService.findOnePost(postId);
        return Response.success(postSearchResponse);
    }

    @GetMapping
    public Response<Page<PostSearchResponse>> findAllPost(@PageableDefault(size = 20, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostSearchResponse> postDtoList = postService.findAllPost(pageable);
        return Response.success(postDtoList);
    }

    @PutMapping("{postId}")
    public Response<PostResponse> modify(@RequestBody PostModifyRequest postModifyRequest,@PathVariable Long postId, Authentication authentication) {
        String username = authentication.getName();
        PostResponse updatePost = postService.update(postModifyRequest, postId,username);
        return Response.success(updatePost);
    }

    @DeleteMapping("/{postId}")
    public Response<PostResponse> delete(@PathVariable Long postId, Authentication authentication) {
        String userName = authentication.getName();
        PostResponse deletePost = postService.deletePost(postId, userName);
        return Response.success(deletePost);
    }
}
