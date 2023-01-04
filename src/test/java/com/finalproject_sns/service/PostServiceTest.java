package com.finalproject_sns.service;

import com.finalproject_sns.domain.Post;
import com.finalproject_sns.domain.User;
import com.finalproject_sns.domain.dto.post.PostModifyRequest;
import com.finalproject_sns.domain.dto.post.PostRequest;
import com.finalproject_sns.domain.dto.post.PostSearchResponse;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.exception.AppException;
import com.finalproject_sns.repository.PostRepository;
import com.finalproject_sns.repository.UserRepository;
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

        AppException AppException = assertThrows(AppException.class,() -> postService.create(new PostRequest(mockPost.getTitle(),mockPost.getBody()), mockUser.getUserName()));

        assertEquals(ErrorCode.USERNAME_NOT_FOUND,AppException.getErrorCode());
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
        PostSearchResponse postDto = postService.findOnePost(post.getId());
        assertEquals(postDto.getUserName(), user.getUserName());
    }

    @Test
    @DisplayName("수정 실패 - 포스트 없음")
    void post_modify_fail() {
        User user = User.builder()
                .id(1L)
                .userName("ajin")
                .password("1234")
                .build();
        Post post = Post.builder()
                .id(1L)
                .title("수정")
                .body("수정 테스트")
                .user(user)
                .build();

        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        AppException AppException1 = assertThrows(AppException.class, () -> postService.update(new PostModifyRequest(post.getTitle(), post.getBody()) ,post.getId(),post.getUser().getUserName()));

        assertEquals(ErrorCode.POST_NOT_FOUND, AppException1.getErrorCode());
    }


    @Test
    @DisplayName("수정 실패 - 작성자!= 유저 ")
    void post_modify_fail2() {
        User user1 = User.builder()
                .id(1L)
                .userName("ajin")
                .password("1234")
                .build();

        User user2 = User.builder()
                .id(1L)
                .userName("najin")
                .password("1234")
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("수정")
                .body("수정 테스트")
                .user(user1)
                .build();

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userRepository.findByUserName(user2.getUserName())).thenReturn(Optional.of(user2));

        AppException AppException1 = assertThrows(AppException.class, () -> postService.update(new PostModifyRequest(post.getTitle(),post.getBody()),post.getUser().getId(),user2.getUserName()));

        assertEquals(ErrorCode.INVALID_PERMISSION, AppException1.getErrorCode());
    }

    @Test
    @DisplayName("수정 실패 - 유저 X ")
    void post_modify_fail3() {
        User user = User.builder()
                .id(1L)
                .userName("ajin")
                .password("1234")
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("수정")
                .body("수정 테스트")
                .user(user)
                .build();

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.empty());

        AppException AppException1 = assertThrows(AppException.class, () -> postService.update(new PostModifyRequest(post.getTitle(),post.getBody()),post.getUser().getId(),user.getUserName()));

        assertEquals(ErrorCode.USERNAME_NOT_FOUND, AppException1.getErrorCode());
    }

    @Test
    @DisplayName("삭제 실패 - 유저 X")
    void post_delete_fail1() {
        User user = User.builder()
                .id(1L)
                .userName("ajin")
                .password("1234")
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("수정")
                .body("수정 테스트")
                .user(user)
                .build();

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.empty());

        AppException AppException1 = assertThrows(AppException.class, () -> postService.deletePost(post.getId(),post.getUser().getUserName()));

        assertEquals(ErrorCode.USERNAME_NOT_FOUND, AppException1.getErrorCode());
    }

    @Test
    @DisplayName("삭제 실패 - 포스트 X")
    void post_delete_fail2() {

        User user = User.builder()
                .id(1L)
                .userName("ajin")
                .password("1234")
                .build();
        Post post = Post.builder()
                .id(1L)
                .title("수정")
                .body("수정 테스트")
                .user(user)
                .build();

        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));

        AppException AppException1 = assertThrows(AppException.class, () -> postService.deletePost(post.getId(),post.getUser().getUserName()));

        assertEquals(ErrorCode.POST_NOT_FOUND, AppException1.getErrorCode());
    }

}
