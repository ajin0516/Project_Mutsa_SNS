package com.finalproject_sns.service;

import com.finalproject_sns.domain.Comment;
import com.finalproject_sns.domain.Post;
import com.finalproject_sns.domain.User;
import com.finalproject_sns.domain.dto.comment.create.CommentCreateRequest;
import com.finalproject_sns.domain.dto.comment.create.CommentCreateResponse;
import com.finalproject_sns.domain.dto.comment.update.CommentUpdateRequest;
import com.finalproject_sns.domain.dto.comment.update.CommentUpdateResponse;
import com.finalproject_sns.exception.AppException;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.repository.CommentRepository;
import com.finalproject_sns.repository.PostRepository;
import com.finalproject_sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

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

    public CommentUpdateResponse update(CommentUpdateRequest commentUpdateRequest, String userName, Long postId, Long id) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "은 존재하지 않는 회원입니다."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 글은 존재하지 않습니다."));

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 댓글은 존재하지 않습니다"));

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
}
