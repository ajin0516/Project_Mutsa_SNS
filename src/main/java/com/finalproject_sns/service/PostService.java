package com.finalproject_sns.service;


import com.finalproject_sns.domain.Post;
import com.finalproject_sns.domain.User;
import com.finalproject_sns.domain.UserRole;
import com.finalproject_sns.domain.dto.post.PostModifyRequest;
import com.finalproject_sns.domain.dto.post.PostRequest;
import com.finalproject_sns.domain.dto.post.PostResponse;
import com.finalproject_sns.domain.dto.post.PostSearchResponse;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.exception.AppException;
import com.finalproject_sns.repository.PostRepository;
import com.finalproject_sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponse create(PostRequest postRequest, String userName) {
        // user 존재하지 않을 때
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "은 존재하지 않는 회원입니다."));

        // user 존재하면 save
        Post savePost = postRepository.save(postRequest.toEntity(user));
        return PostResponse.builder()
                .message("포스트 등록완료")
                .postId(savePost.getId())
                .build();
    }

    public PostSearchResponse findOnePost(Long postId) {

        // 게시글 존재하지 않을 떄
        Post post = postRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 글은 존재하지 않습니다."));

        return PostSearchResponse.toDto(post);
    }

    public Page<PostSearchResponse> findAllPost(Pageable pageable) {
        Page<Post> findAll = postRepository.findAll(pageable);
        Page<PostSearchResponse> postDtoList = PostSearchResponse.toDtoList(findAll);
        return postDtoList;
    }


    @Transactional
    public PostResponse update(PostModifyRequest request, Long postId, String userName) {
        // 게시글 존재하지 않을 떄
        Post post = postRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 글은 존재하지 않습니다."));

        // user 존재하지 않을 때
        User user = userRepository.findByUserName(userName).orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "은 존재하지 않는 회원입니다."));

        // 해당 글 작성자나 ADMIN만 수정 가능
        if (!post.getUser().getUserName().equals(userName) && !user.getRole().name().equals(UserRole.ADMIN.name())) {
            log.info("!post.getUser().getUserName().equals(userName)={}",!post.getUser().getUserName().equals(userName));
            log.info("!user.getRole().name().equals(UserRole.ADMIN.name()={}",!user.getRole().name().equals(UserRole.ADMIN.name()));
            throw new AppException(ErrorCode.INVALID_PERMISSION,"사용자가 권한이 없습니다.");
        }
        log.info("=====if 벗어남====");
        log.info("!post.getUser().getUserName().equals(userName)={}",!post.getUser().getUserName().equals(userName));
        log.info("!user.getRole().name().equals(UserRole.ADMIN.name()={}",!user.getRole().name().equals(UserRole.ADMIN.name()));

        post.update(request.getTitle(),request.getBody());
        postRepository.save(post);
        return PostResponse.builder()
                .message("포스트 수정 완료")
                .postId(postId)
                .build();
    }


    @Transactional
    public PostResponse deletePost(Long postId, String userName) {
        // user 존재하지 않을 때
        User user = userRepository.findByUserName(userName).orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "은 존재하지 않는 회원입니다."));

        // 게시글 존재하지 않을 떄
        Post post = postRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 글은 존재하지 않습니다."));

        // 해당 글 작성자나 ADMIN만 삭제 가능
        if (!post.getUser().getUserName().equals(userName) && !user.getRole().name().equals(UserRole.ADMIN.name())) {
            throw new AppException(ErrorCode.INVALID_PERMISSION,"사용자가 권한이 없습니다.");
        }

        postRepository.deleteById(postId);
        return PostResponse.builder()
                .message("포스트 삭제 완료")
                .postId(postId)
                .build();
    }


}
