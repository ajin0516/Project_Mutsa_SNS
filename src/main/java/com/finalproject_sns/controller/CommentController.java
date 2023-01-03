package com.finalproject_sns.controller;

import com.finalproject_sns.domain.dto.Response;
import com.finalproject_sns.domain.dto.comment.create.CommentCreateRequest;
import com.finalproject_sns.domain.dto.comment.create.CommentCreateResponse;
import com.finalproject_sns.domain.dto.comment.list.CommentListResponse;
import com.finalproject_sns.domain.dto.comment.update.CommentUpdateRequest;
import com.finalproject_sns.domain.dto.comment.update.CommentUpdateResponse;
import com.finalproject_sns.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postsId}/comments")
    public Response<CommentCreateResponse> create(@RequestBody CommentCreateRequest commentCreateRequest, @PathVariable Long postsId, Authentication authentication) {
        String userName = authentication.getName();
        CommentCreateResponse commentCreateResponse = commentService.create(commentCreateRequest, userName, postsId);
        return Response.success(commentCreateResponse);
    }

    @PutMapping("/posts/{postId}/comments/{id}")
    public Response<CommentUpdateResponse> update(@RequestBody CommentUpdateRequest commentUpdateRequest, @PathVariable Long postId, @PathVariable Long id, Authentication authentication) {
        String userName = authentication.getName();
        CommentUpdateResponse commentUpdateResponse = commentService.update(commentUpdateRequest, userName, postId,id);
        return Response.success(commentUpdateResponse);
    }

    @GetMapping("/posts/{postId}/comments")
    public Response<Page<CommentListResponse>> list(@PageableDefault(size = 10, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable Long postId) {
        Page<CommentListResponse> commentListResponses = commentService.commentList(postId, pageable);
        return Response.success(commentListResponses);
    }
}
