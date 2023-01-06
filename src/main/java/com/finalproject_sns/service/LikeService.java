package com.finalproject_sns.service;

import com.finalproject_sns.domain.Like;
import com.finalproject_sns.domain.Post;
import com.finalproject_sns.domain.User;
import com.finalproject_sns.exception.AppException;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.repository.LikeRepository;
import com.finalproject_sns.repository.PostRepository;
import com.finalproject_sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public String likeClick(Long postId, String userName) {

        // 유저 없을 때
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "존재하지 않는 회원입니다."));

        // 포스트 없을 때
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "존재하지 않는 포스트입니다."));


//          Optional<Like> likePost = likeRepository.findByPostAndUser(post, user);
//          log.info("likePost={}", likePost);
//
//        if (likePost.isPresent() && likePost.get().isDeletedAt() == false) {
//            log.info("likePost.get().isDeletedAt()={}",likePost.get().isDeletedAt());
//            log.info("delete 작동 중");
//            likeRepository.delete(Like()); // true(1)
//            return "좋아요를 취소했습니다.";
//        }
//        if (likePost.isPresent() && likePost.get().isDeletedAt() != false) {
//            likeRepository.save(Like.saveAndDelete(user, post)); // false(0)
//            throw new AppException(ErrorCode.ALREADY_LIKE_CLICK, "한번 취소한 좋아요는 다시 할 수 없숴!");
//        }

//        likeRepository.save(Like.saveAndDelete(user, post));
//        return "좋아요를 눌렀습니다.";

//    좋아요 안 누른 상태
//    public boolean isNotAlreadyLike(User user, Post post) {
//       return likeRepository.findByPostAndUser(post, user).isEmpty();
//    }

//        log.info("likePost.get().isDeletedAt()",likePost.get().isDeletedAt());

        Optional<Like> likePost = likeRepository.findByUserAndPost(user, post);

        if(!likePost.isPresent()){
            likeRepository.save(new Like(user, post)); // 상태 : null
            return "좋아요를 눌렀습니다.";
        }else  {
            if (likePost.get().getDeletedAt() == null) {
                likeRepository.delete(likePost.get()); // get()을 써줘야 삭제가 된다....
                return "좋아요를 취소했습니다."; // 상태 : 현재시간
            }else {
                likeRepository.reSave(likePost.get().getId()); // 상태 : null
                return "좋아요를 눌렀습니다.";
            }
        }

    }

    public Integer likeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 존재하지 않습니다."));
        User user = userRepository.findByUserName(post.getUser().getUserName())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "존재하지 않는 회원입니다."));
        return likeRepository.countByPost(post);
    }
}



