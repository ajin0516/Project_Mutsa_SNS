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
        Alarm alarm = alarmRepository.findByUserAndTargetId(user ,post.getId());

        String message = AlarmType.NEW_LIKE_ON_POST.getMessage();
        AlarmType alarmType = AlarmType.NEW_LIKE_ON_POST;

        if(!likePost.isPresent()){
            likeRepository.save(new Like(user, post)); // 상태 : null
            alarmRepository.save(new Alarm(user,post,message,alarmType));
            return "좋아요를 눌렀습니다.";
        }else  {
            if (likePost.get().getDeletedAt() == null) {
                likeRepository.delete(likePost.get()); // get()을 써줘야 삭제가 된다....
                alarmRepository.delete(alarm);
                return "좋아요를 취소했습니다."; // 상태 : 현재시간
            }else {
                likeRepository.reSave(likePost.get().getId()); // 상태 : null
                alarmRepository.reSave(alarm.getId());
                return "좋아요를 눌렀습니다.";
            }
        }
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



