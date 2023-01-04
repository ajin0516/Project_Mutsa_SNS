package com.finalproject_sns.domain.dto.comment.delete;

import com.finalproject_sns.domain.Comment;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentDeleteResponse {

    private String message;
    private Long id;

    @Builder
    public CommentDeleteResponse(String message, Long id) {
        this.message = message;
        this.id = id;
    }

    public static CommentDeleteResponse toDto(Comment comment) {
        return CommentDeleteResponse.builder()
                .message("댓글 삭제 완료")
                .id(comment.getId())
                .build();
    }

}
