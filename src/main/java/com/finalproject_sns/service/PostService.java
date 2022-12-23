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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostResponse create(PostRequest postRequest, String userName) {
        // user가 존재하지 않을 때
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UserAppException(ErrorCode.USERNAME_NOT_FOUND, userName + "은 존재하지 않는 회원입니다."));

        // user가 존재하면 post save
        Post savePost = postRepository.save(postRequest.toEntity(user));
        return PostResponse.builder()
                .postId(savePost.getId())
                .message("포스트 등록완료")
                .build();
    }

    public PostSearchResponse findById(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        Post post = postOptional.orElseThrow(() -> new UserAppException(ErrorCode.POST_NOT_FOUND, "해당 글은 존재하지 않습니다."));


        return PostSearchResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .userName(post.getUser().getUserName())
                .createAt(post.getCreateAt())
                .lastModifiedAt(post.getLastModifiedAt())
                .build();
    }
}
