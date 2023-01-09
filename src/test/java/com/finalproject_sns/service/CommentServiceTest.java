package com.finalproject_sns.service;

import com.finalproject_sns.domain.Comment;
import com.finalproject_sns.domain.Post;
import com.finalproject_sns.domain.User;
import com.finalproject_sns.domain.dto.comment.delete.CommentDeleteResponse;
import com.finalproject_sns.domain.dto.comment.update.CommentUpdateRequest;
import com.finalproject_sns.exception.AppException;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.repository.AlarmRepository;
import com.finalproject_sns.repository.CommentRepository;
import com.finalproject_sns.repository.PostRepository;
import com.finalproject_sns.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommentServiceTest {

    CommentService commentService;

    CommentRepository commentRepository = mock(CommentRepository.class);
    PostRepository postRepository = mock(PostRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    AlarmRepository alarmRepository = mock(AlarmRepository.class);

    User mockUser = mock(User.class);
    Post mockPost = mock(Post.class);
    Comment mockComment = mock(Comment.class);

    @BeforeEach
    void before() {
        commentService = new CommentService(commentRepository, postRepository, userRepository, alarmRepository);
    }

    @Test
    @DisplayName("댓글 수정 실패 - 포스트 없음")
    void comment_update_fail_no_post() {

        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.empty());
        when(userRepository.findByUserName(mockUser.getUserName())).thenReturn(Optional.of(mockUser));
        when(commentRepository.findById(mockComment.getId())).thenReturn(Optional.of(mockComment));

        AppException appException = assertThrows(AppException.class, () -> commentService.update(new CommentUpdateRequest(), mockUser.getUserName(), mockPost.getId(), mockComment.getId()));

        assertEquals(ErrorCode.POST_NOT_FOUND, appException.getErrorCode());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 작성자!=유저")
    void comment_update_fail_not_match() {

        User commentUser = User.builder().id(2L).userName("postUser").password("123").build();

        User loginUser = User.builder().id(1L).userName("loginUser").password("456").build();

        Comment mockComment = Comment.builder().id(1L).user(commentUser).comment("testComment").post(mockPost).build();

        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));
        when(userRepository.findByUserName(loginUser.getUserName())).thenReturn(Optional.of(loginUser));
        when(commentRepository.findById(mockComment.getId())).thenReturn(Optional.of(mockComment));

        AppException appException = assertThrows(
                AppException.class, () -> commentService.update(new CommentUpdateRequest(), loginUser.getUserName(), mockPost.getId(), mockComment.getId()));
        assertEquals(ErrorCode.INVALID_PERMISSION, appException.getErrorCode());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 유저 없음")
    void comment_update_fail_no_user() {

        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));
        when(userRepository.findByUserName(mockUser.getUserName())).thenReturn(Optional.empty());
        when(commentRepository.findById(mockComment.getId())).thenReturn(Optional.of(mockComment));

        AppException appException = assertThrows(
                AppException.class, () -> commentService.update(new CommentUpdateRequest(), mockUser.getUserName(), mockPost.getId(), mockComment.getId()));
        assertEquals(ErrorCode.USERNAME_NOT_FOUND, appException.getErrorCode());
    }

    @Test
    @DisplayName("유저 X")
    void comment_delete_fail_no_user() throws Exception {

        when(commentRepository.findById(mockComment.getId())).thenReturn(Optional.of(mockComment));
        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));
        when(userRepository.findByUserName(mockUser.getUserName())).thenReturn(Optional.empty());

        AppException appException = assertThrows(
                AppException.class, () -> commentService.delete(mockUser.getUserName(), mockPost.getId(), mockComment.getId())
        );
        assertEquals(ErrorCode.USERNAME_NOT_FOUND, appException.getErrorCode());
    }

    @Test
    @DisplayName("댓글 X")
    void comment_delete_fail_no_comment() throws Exception {

        when(commentRepository.findById(mockComment.getId())).thenReturn(Optional.empty());
        when(userRepository.findByUserName(mockUser.getUserName())).thenReturn(Optional.of(mockUser));
        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));

        AppException appException = assertThrows(
                AppException.class, () -> commentService.delete(mockUser.getUserName(), mockPost.getId(), mockComment.getId())
        );

        assertEquals(ErrorCode.COMMENT_NOT_FOUND,appException.getErrorCode());
    }
}