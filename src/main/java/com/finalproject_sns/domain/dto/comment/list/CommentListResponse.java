package com.finalproject_sns.domain.dto.comment.list;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.finalproject_sns.domain.Comment;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
public class CommentListResponse {
    private Long id;
    private String comment;
    private String userName;
    private Long postId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createAt;;


    @Builder
    public CommentListResponse(Long id, String comment, String userName, Long postId, LocalDateTime createAt) {
        this.id = id;
        this.comment = comment;
        this.userName = userName;
        this.postId = postId;
        this.createAt = createAt;
    }

    /* Entity -> Dto 변환 */
    public static Page<CommentListResponse> toDtoList(Page<Comment> commentList) {
        Page<CommentListResponse> commentDtoList = commentList.map(c -> CommentListResponse.builder()
                .id(c.getId())
                .comment(c.getComment())
                .userName(c.getPost().getUser().getUserName()) // post작성한 user의 userName을 가져와야 함
                .postId(c.getPost().getId())
                .createAt(c.getCreateAt())
                .build());
        return commentDtoList;
    }
}

