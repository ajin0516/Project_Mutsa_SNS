package com.finalproject_sns.domain.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.finalproject_sns.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
public class MyFeedResponse {

    private Long id;
    private String title;
    private String body;
    private String userName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createAt;

    @Builder
    public MyFeedResponse(Long id, String title, String body, String userName, LocalDateTime createAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.userName = userName;
        this.createAt = createAt;
    }

    /* Page<Post> -> Page<Dto> 변환처리 */
    public static Page<MyFeedResponse> toDtoList(Page<Post> postList) {
        Page<MyFeedResponse> postDtoList = postList.map( p -> MyFeedResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .userName(p.getUser().getUserName())
                .body(p.getBody())
                .createAt(p.getCreateAt())
                .build());
        return postDtoList;
    }
}
