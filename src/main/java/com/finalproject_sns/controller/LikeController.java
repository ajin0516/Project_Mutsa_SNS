package com.finalproject_sns.controller;

import com.finalproject_sns.domain.dto.Response;
import com.finalproject_sns.service.LikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "좋아요")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{postId}/likes")
    @ApiOperation(value = "좋아요 누르기", notes = "Token필요, 좋아요/좋아요 취소")
    public Response likeClick(@PathVariable Long postId, Authentication authentication) {
        String userName = authentication.getName();
        log.info("userName={}", userName);
        String result = likeService.likeClick(postId, userName);
        log.info("result={}", result);
        return Response.success(result);
    }

    @ApiOperation(value = "좋아요 조회", notes = "모두 접근가능")
    @GetMapping("/{postsId}/likes")
    public Response getCount(@PathVariable Long postsId) {
        Integer count = likeService.likeCount(postsId);
        return Response.success(count);
    }
}
