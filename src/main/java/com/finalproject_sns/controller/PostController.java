package com.finalproject_sns.controller;

import com.finalproject_sns.domain.dto.Response;
import com.finalproject_sns.domain.dto.post.*;
import com.finalproject_sns.service.PostService;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation(value = "포스트 작성", notes = "Token 필요")
    @PostMapping
    public Response<PostResponse> write(@RequestBody PostRequest postRequest, Authentication authentication) {
        String userName = authentication.getName();
        PostResponse postResponse = postService.create(postRequest, userName);
        return Response.success(postResponse);
    }

    @ApiOperation(value = "포스트 단건 조회")
    @GetMapping("/{postId}")
    public Response<PostSearchResponse> findOnePost(@PathVariable Long postId) {
        PostSearchResponse postSearchResponse = postService.findOnePost(postId);
        return Response.success(postSearchResponse);
    }

    @ApiOperation(value = "포스트 전체 조회", notes = "최신 작성 순으로 내림차순")
    @GetMapping
    public Response<Page<PostSearchResponse>> findAllPost(@PageableDefault(size = 20, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostSearchResponse> postDtoList = postService.findAllPost(pageable);
        return Response.success(postDtoList);
    }

    @ApiOperation(value = "포스트 수정", notes = "Token 필요, 작성자와 관리자만 가능")
    @PutMapping("{postId}")
    public Response<PostResponse> update(@RequestBody PostModifyRequest postModifyRequest,@PathVariable Long postId, Authentication authentication) {
        String username = authentication.getName();
        PostResponse updatePost = postService.update(postModifyRequest, postId,username);
        return Response.success(updatePost);
    }

    @ApiOperation(value = "포스트 삭제", notes = "Token 필요, 작성자와 관리자만 가능")
    @DeleteMapping("/{postId}")
    public Response<PostResponse> delete(@PathVariable Long postId, Authentication authentication) {
        String userName = authentication.getName();
        PostResponse deletePost = postService.deletePost(postId, userName);
        return Response.success(deletePost);
    }

    @ApiOperation(value = "마이 피드 조회", notes = "Token 필요, 본인의 피드만 조회 가능")
    @GetMapping("/my")
    public Response<Page<MyFeedResponse>> findByUser(@PageableDefault(size = 20, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) {
        String userName = authentication.getName();
        Page<MyFeedResponse> findByUserId = postService.findByUser(userName, pageable);
        return Response.success(findByUserId);
    }
}
