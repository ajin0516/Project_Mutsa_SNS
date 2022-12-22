package com.finalproject_sns.service;


import com.finalproject_sns.domain.Post;
import com.finalproject_sns.domain.User;
import com.finalproject_sns.domain.dto.post.PostDto;
import com.finalproject_sns.domain.dto.post.PostRequest;
import com.finalproject_sns.domain.dto.user.UserDto;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.exception.UserAppException;
import com.finalproject_sns.repository.PostRepository;
import com.finalproject_sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostDto create(PostRequest postRequest, String userName) {
        // user가 존재하지 않을 때
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UserAppException(ErrorCode.USERNAME_NOT_FOUND, userName + "은 존재하지 않는 회원입니다."));

        // user가 존재하면 post save
        Post savePost = postRepository.save(postRequest.toEntity(user));
        return PostDto.builder()
                .id(savePost.getId())
                .title(savePost.getTitle())
                .body(savePost.getBody())
                .build();
    }
}
