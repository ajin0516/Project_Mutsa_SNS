package com.finalproject_sns.service;

import com.finalproject_sns.domain.Post;
import com.finalproject_sns.domain.User;
import com.finalproject_sns.domain.dto.post.PostRequest;
import com.finalproject_sns.domain.dto.post.PostResponse;
import com.finalproject_sns.domain.dto.post.PostSearchResponse;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.exception.UserAppException;
import com.finalproject_sns.repository.PostRepository;
import com.finalproject_sns.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PostServiceTest {

    PostService postService;

    PostRepository postRepository = mock(PostRepository.class);
    UserRepository userRepository = mock(UserRepository.class);



    @BeforeEach
    void before() {
        postService = new PostService(postRepository, userRepository);
    }

    @Test
    @DisplayName("등록 성공")
    void post_success() {

        Post mockPost = mock(Post.class);
        User mockUser = mock(User.class);

        when(userRepository.findByUserName(mockUser.getUserName())).thenReturn(Optional.of(mockUser));
        when(postRepository.save(any())).thenReturn(mockPost);

        assertDoesNotThrow(() -> postService.create(new PostRequest(mockPost.getTitle(),mockPost.getBody()), mockUser.getUserName()));
    }

    @Test
    @DisplayName("등록 실패 - 유저 없음")
    void post_fail() {

        Post mockPost = mock(Post.class);
        User mockUser = mock(User.class);

        when(userRepository.findByUserName(mockUser.getUserName())).thenReturn(Optional.empty());
        when(postRepository.save(any())).thenReturn(mockPost);

        UserAppException userAppException = assertThrows(UserAppException.class,() -> postService.create(new PostRequest(mockPost.getTitle(),mockPost.getBody()), mockUser.getUserName()));

        assertEquals(ErrorCode.USERNAME_NOT_FOUND,userAppException.getErrorCode());
    }

    @Test
    @DisplayName("단건 조회 성공")
    void post_findOne_success() {

        User user = User.builder()
                .id(1L)
                .userName("ajin")
                .password("1234")
                .build();
        Post post = Post.builder()
                .id(1L)
                .title("조회")
                .body("단건조회 테스트")
                .user(user)
                .build();

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        PostSearchResponse postDto = postService.findById(post.getId());
        assertEquals(postDto.getUserName(), user.getUserName());
    }

    // 실패
    @Test
    @DisplayName("수정 실패 - 포스트 없음")
    void post_modify_fail() {

        Post mockPost = mock(Post.class);

        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.empty());

        UserAppException userAppException1 = assertThrows(UserAppException.class, () -> postService.update(new PostRequest(mockPost.getTitle(),mockPost.getBody()),mockPost.getId(),mockPost.getUser().getUserName()));

        assertEquals(ErrorCode.POST_NOT_FOUND, userAppException1.getErrorCode().getHttpStatus());
    }

}
