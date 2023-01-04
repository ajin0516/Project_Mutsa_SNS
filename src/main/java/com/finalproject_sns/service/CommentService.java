package com.finalproject_sns.service;

import com.finalproject_sns.domain.Comment;
import com.finalproject_sns.domain.Post;
import com.finalproject_sns.domain.User;
import com.finalproject_sns.domain.dto.comment.create.CommentCreateRequest;
import com.finalproject_sns.domain.dto.comment.create.CommentCreateResponse;
import com.finalproject_sns.domain.dto.comment.delete.CommentDeleteResponse;
import com.finalproject_sns.domain.dto.comment.list.CommentListResponse;
import com.finalproject_sns.domain.dto.comment.update.CommentUpdateRequest;
import com.finalproject_sns.domain.dto.comment.update.CommentUpdateResponse;
import com.finalproject_sns.exception.AppException;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.repository.CommentRepository;
import com.finalproject_sns.repository.PostRepository;
import com.finalproject_sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentCreateResponse create(CommentCreateRequest commentCreateRequest, String userName, Long postId) {

        // 게시글 없을 때
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 글은 존재하지 않습니다."));

        // user 존재하지 않을 떄
        userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "은 존재하지 않는 회원입니다."));

        Comment saveComment = commentRepository.save(commentCreateRequest.toEntity(post));
        return CommentCreateResponse.of(saveComment);
    }

    @Transactional
    public CommentUpdateResponse update(CommentUpdateRequest commentUpdateRequest, String userName, Long postId, Long commentId) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "은 존재하지 않는 회원입니다."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 글은 존재하지 않습니다."));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND, "해당 댓글은 존재하지 않습니다"));

        // 작성자 불일치
        log.info("userName={}",userName);
        log.info("comment.getUser().getUserName()={}",comment.getUser().getUserName());
        if (!userName.equals(comment.getUser().getUserName())) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "사용자 권한이 없습니다.");
        }

        comment.update(commentUpdateRequest.getComment());
        commentRepository.save(comment);
        return CommentUpdateResponse.of(comment);
    }

    public Page<CommentListResponse> commentList(Long id, Pageable pageable) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "포스트가 존재하지 않습니다."));

        log.info("post={}",post);
        Page<Comment> byPostId = commentRepository.findByPostId(post.getId(), pageable);
        Page<CommentListResponse> commentListResponses = CommentListResponse.toDtoList(byPostId);
        return commentListResponses;
    }

    @Transactional
    public CommentDeleteResponse delete(String userName, Long postId, Long commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "존재하지 않는 포스트입니다."));
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "는 존재하지 않는 회원입니다."));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND, "존재하지 않는 댓글입니다."));

        if (!comment.getUser().getUserName().equals(userName)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "작성자만 삭제 가능합니다.");
        }
        commentRepository.delete(comment);
        return CommentDeleteResponse.toDto(comment);
    }
}
