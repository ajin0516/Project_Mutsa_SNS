package com.finalproject_sns.service;

import com.finalproject_sns.domain.*;
import com.finalproject_sns.exception.AppException;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.repository.AlarmRepository;
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
    private final AlarmRepository alarmRepository;

    @Transactional
    public String likeClick(Long postId, String userName) {

        // 유저 없을 때
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "존재하지 않는 회원입니다."));

        // 포스트 없을 때
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "존재하지 않는 포스트입니다."));

        Optional<Like> likePost = likeRepository.findByUserAndPost(user, post);

//        Optional<Alarm> alarm = alarmRepository.findById(user.getId());
//        Alarm alarm = alarmRepository.findByUserAndTargetId(user, post.getId());

        Alarm alarm = alarmRepository.findByUserAndTargetId(post.getUser(), post.getId());

        log.info("user.getId()={}", user.getId());
        if (likePost.isPresent() && likePost.get().getDeletedAt() == null) {
            likeRepository.delete(likePost.get());
            if(user.getId() != post.getUser().getId()) { // 본인의 포스트가 아닌 다른 회원의 포스트에 좋아요 취소 시 삭제( 알람 조회 시 본인한테 좋아요한 건 제외시키기 위함)
                alarmRepository.delete(alarm);
            }
            return "좋아요를 취소했습니다.";  // 상태 : 삭제 시간
        } else if (likePost.isPresent() && likePost.get().getDeletedAt() != null) {
            likeRepository.reSave(likePost.get().getId());
            if( user.getId() != post.getUser().getId()) {  // 본인의 포스트가 아닌 다른 회원의 포스트에 좋아요 시 재저장( 알람 조회 시 본인한테 좋아요한 건 제외시키기 위함)
                alarmRepository.reSave(alarm.getId());
            }
            return "좋아요를 눌렀습니다.";  // 상태 : null
        }

        log.info("user.getId()={}",user.getId());
        log.info("post.getUser().getId()={}",post.getUser().getId());
        likeRepository.save(new Like(user, post));
        if( user.getId() != post.getUser().getId()) {  // 본인의 포스트가 아닌 다른 회원의 포스트에 좋아요 시 저장( 알람 조회 시 본인한테 좋아요한 건 제외시키기 위함)
            alarmRepository.save(new Alarm(user, post, AlarmType.NEW_LIKE_ON_POST));
        }
        return "좋아요를 눌렀습니다.";
    }

    @Transactional(readOnly = true)
    public Integer likeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 존재하지 않습니다."));
        User user = userRepository.findByUserName(post.getUser().getUserName())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "존재하지 않는 회원입니다."));
        return likeRepository.countByPost(post);
    }
}



