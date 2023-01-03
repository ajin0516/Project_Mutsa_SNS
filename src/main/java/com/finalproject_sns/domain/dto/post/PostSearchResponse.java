package com.finalproject_sns.domain.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.finalproject_sns.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class PostSearchResponse {

    private Long id;
    private String title;
    private String body;
    private String userName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastModifiedAt;


    /* Entity -> Dto 변환처리*/
    public static PostSearchResponse toDto(Post post){
            return PostSearchResponse.builder()
            .id(post.getId())
            .title(post.getTitle())
            .body(post.getBody())
            .userName(post.getUser().getUserName())
            .createAt(post.getCreateAt())
            .lastModifiedAt(post.getLastModifiedAt())
            .build();
    }

    /* Page<Post> -> Page<Dto> 변환처리 */
    /* List 로 받으면 변환이 더 간단  */
    public static Page<PostSearchResponse> toDtoList(Page<Post> postList) {
        Page<PostSearchResponse> postDtoList = postList.map( m -> PostSearchResponse.builder()
                .id(m.getId())
                .title(m.getTitle())
                .userName(m.getUser().getUserName())
                .body(m.getBody())
                .createAt(m.getCreateAt())
                .lastModifiedAt(m.getLastModifiedAt())
                .build());
        return postDtoList;
    }
}
