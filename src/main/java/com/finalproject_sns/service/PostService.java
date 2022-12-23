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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostResponse create(PostRequest postRequest, String userName) {
        // user 존재하지 않을 때
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UserAppException(ErrorCode.USERNAME_NOT_FOUND, userName + "은 존재하지 않는 회원입니다."));

        // user 존재하면 save
        Post savePost = postRepository.save(postRequest.toEntity(user));
        return PostResponse.builder()
                .postId(savePost.getId())
                .message("포스트 등록완료")
                .build();
    }

    public PostSearchResponse findById(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);

        // 존재하지 않는 게시글
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

    public Page<PostSearchResponse> findAllPost(Pageable pageable) {
        Page<Post> findAll = postRepository.findAll(pageable);
        Page<PostSearchResponse> postDtoList = PostSearchResponse.toDtoList(findAll);
        return postDtoList;
    }

    public PostResponse update(PostRequest request, Long postId, String userName) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new UserAppException(ErrorCode.POST_NOT_FOUND, "해당 글은 존재하지 않습니다."));
        User user = userRepository.findByUserName(userName).orElseThrow(() -> new UserAppException(ErrorCode.USERNAME_NOT_FOUND, userName + "은 존재하지 않는 회원입니다."));
        if (post.getUser().getUserName() != request.toEntity(user).getUser().getUserName()) {
            throw new UserAppException(ErrorCode.INVALID_PERMISSION,"사용자가 권한이 없습니다.");
        }
        post.update(request.getTitle(),request.getBody());
        postRepository.save(post);
        return PostResponse.builder()
                .message("포스트 수정 완료")
                .postId(postId)
                .build();
    }


    public PostResponse deletePost(Long postId, String userName) {
        // user 존재하지 않을 때
        userRepository.findByUserName(userName).orElseThrow(() -> new UserAppException(ErrorCode.USERNAME_NOT_FOUND, userName + "은 존재하지 않는 회원입니다."));

        // 게시글 존재하지 않을 떄
        postRepository.findById(postId).orElseThrow(() -> new UserAppException(ErrorCode.POST_NOT_FOUND, "해당 글은 존재하지 않습니다."));

        postRepository.deleteById(postId);
        return PostResponse.builder()
                .postId(postId)
                .message("포스트 삭제 완료")
                .build();
    }
}
