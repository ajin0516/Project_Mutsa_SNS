package com.finalproject_sns.service;

import com.finalproject_sns.domain.Post;
import com.finalproject_sns.domain.User;
import com.finalproject_sns.domain.dto.post.PostRequest;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.exception.UserAppException;
import com.finalproject_sns.repository.PostRepository;
import com.finalproject_sns.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PostServiceTest {

    PostService postService;

    PostRepository postRepository = mock(PostRepository.class);
    UserRepository userRepository = mock(UserRepository.class);

    private final Long USER_ID = 1L;
    private final String USERNAME = "testUser";
    private final String PASSWORD = "testPwd";
    private final User USER = User.builder()
            .id(USER_ID)
            .userName(USERNAME)
            .password(PASSWORD)
            .build();

    private final String TITLE = "testTitle";
    private final String BODY = "testBody";
    private final Long POST_ID = 1L;

    private final Post POST = Post.builder()
            .id(POST_ID)
            .title(TITLE)
            .body(BODY)
            .user(USER)
            .build();


    @BeforeEach
    void before() {
        postService = new PostService(postRepository, userRepository);
    }

    @Test
    @DisplayName("등록 성공")
    void post_success() {

        Post mockPost = mock(Post.class);
        User mockUser = mock(User.class);

        when(userRepository.findByUserName(USERNAME)).thenReturn(Optional.of(mockUser));
        when(postRepository.save(any())).thenReturn(mockPost);

        assertDoesNotThrow(() -> postService.create(new PostRequest(mockPost.getTitle(),mockPost.getBody()), USERNAME));
    }

    @Test
    @DisplayName("등록 실패 - 유저 없음")
    void post_fail() {

        Post mockPost = mock(Post.class);

        when(userRepository.findByUserName(USERNAME)).thenReturn(Optional.empty());
        when(postRepository.save(any())).thenReturn(mockPost);

        UserAppException userAppException = assertThrows(UserAppException.class,() ->postService.create(new PostRequest(mockPost.getTitle(),mockPost.getBody()), USERNAME));

        assertEquals(ErrorCode.USERNAME_NOT_FOUND, userAppException.getErrorCode());
    }
}
