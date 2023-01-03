package com.finalproject_sns.domain.dto.comment.update;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.finalproject_sns.domain.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentUpdateResponse {

    private Long id;
    private String comment;
    private String userName;
    private Long postId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastModifiedAt;


    @Builder
    public CommentUpdateResponse(Long id, String comment, String userName, Long postId, LocalDateTime createAt, LocalDateTime lastModifiedAt) {
        this.id = id;
        this.comment = comment;
        this.userName = userName;
        this.postId = postId;
        this.createAt = createAt;
        this.lastModifiedAt = lastModifiedAt;
    }

    /* Entity -> Dto 변환 */
    public static CommentUpdateResponse of(Comment comment) {
        return CommentUpdateResponse.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .userName(comment.getUser().getUserName())
                .postId(comment.getPost().getId())
                .createAt(comment.getCreateAt())
                .lastModifiedAt(comment.getLastModifiedAt())
                .build();
    }
}

