package com.finalproject_sns.domain.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PostResponse {

    private String message;
    private Long postId;

}
