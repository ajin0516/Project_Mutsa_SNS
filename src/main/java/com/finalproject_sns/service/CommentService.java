package com.finalproject_sns.service;

import com.finalproject_sns.domain.*;
import com.finalproject_sns.domain.dto.comment.create.CommentCreateRequest;
import com.finalproject_sns.domain.dto.comment.create.CommentCreateResponse;
import com.finalproject_sns.domain.dto.comment.delete.CommentDeleteResponse;
import com.finalproject_sns.domain.dto.comment.list.CommentListResponse;
import com.finalproject_sns.domain.dto.comment.update.CommentUpdateRequest;
import com.finalproject_sns.domain.dto.comment.update.CommentUpdateResponse;
import com.finalproject_sns.exception.AppException;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.repository.AlarmRepository;
import com.finalproject_sns.repository.CommentRepository;
import com.finalproject_sns.repository.PostRepository;
import com.finalproject_sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;

    @Transactional
    public CommentCreateResponse create(CommentCreateRequest commentCreateRequest, String userName, Long postId) {

        // 게시글 없을 때
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 글은 존재하지 않습니다."));

        // user 존재하지 않을 떄
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "은 존재하지 않는 회원입니다."));


        Comment saveComment = commentRepository.save(commentCreateRequest.toEntity(post, user));
        if (user.getId() != post.getUser().getId()) {  // 본인이 작성한 포스트에 본인이 댓글을 남기면 댓글 알람 저장 안함
            alarmRepository.save(new Alarm(user, post, AlarmType.NEW_COMMENT_ON_POST));
        }
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
        log.info("userName={}", userName);
        log.info("comment.getUser().getUserName()={}", comment.getUser().getUserName());

        validateCheck(userName, comment, user);

        comment.update(commentUpdateRequest.getComment());
        commentRepository.save(comment);
        return CommentUpdateResponse.of(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentListResponse> commentList(Long id, Pageable pageable) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "포스트가 존재하지 않습니다."));

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

        log.info("commentId={}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND, "존재하지 않는 댓글입니다."));

        validateCheck(userName, comment,user);

        Alarm alarm = alarmRepository.findByUserAndTargetId(post.getUser(), post.getId());

        commentRepository.delete(comment);

        log.info("user.getId()={}, post.getUser().getId()={}",user.getId(), post.getUser().getId());
        if (user.getId() != post.getUser().getId()) { // 본인이 작성한 포스트에 본인이 댓글 남기면 알람을 저장 안하기 때문에 삭제도 암함
            alarmRepository.delete(alarm);
        }

        return CommentDeleteResponse.toDto(comment);
    }

    private static void validateCheck(String userName, Comment comment, User user) {
        if (!userName.equals(comment.getUser().getUserName()) && !user.getRole().equals(UserRole.ADMIN)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "작성자만 가능합니다.");
        }
    }

}
