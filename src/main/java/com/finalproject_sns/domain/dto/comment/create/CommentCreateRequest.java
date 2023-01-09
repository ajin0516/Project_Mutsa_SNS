package com.finalproject_sns.domain.dto.comment.create;

import com.finalproject_sns.domain.Comment;
import com.finalproject_sns.domain.Post;
import com.finalproject_sns.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateRequest {

    private String comment;

    public Comment toEntity(Post post, User user) {
        return Comment.builder()
                .comment(this.comment)
                .post(post)
                .user(user)
                .build();
    }
}
