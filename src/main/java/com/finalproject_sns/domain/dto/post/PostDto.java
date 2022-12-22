package com.finalproject_sns.domain.dto.post;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostDto {
    private Long id;
    private String title;
    private String body;
}
