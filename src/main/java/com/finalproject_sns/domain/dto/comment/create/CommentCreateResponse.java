package com.finalproject_sns.domain.dto.comment.create;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.finalproject_sns.domain.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentCreateResponse {

    private Long id;
    private String comment;
    private String userName;
    private Long postId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createAt;

    @Builder
    public CommentCreateResponse(Long id, String comment, String userName, Long postId, LocalDateTime createAt) {
        this.id = id;
        this.comment = comment;
        this.userName = userName;
        this.postId = postId;
        this.createAt = createAt;
    }

    /* Entity -> Dto 변환 */
    public static CommentCreateResponse of(Comment comment) {
        return CommentCreateResponse.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .userName(comment.getUser().getUserName())
                .postId(comment.getPost().getId())
                .createAt(comment.getCreateAt())
                .build();
    }
}
