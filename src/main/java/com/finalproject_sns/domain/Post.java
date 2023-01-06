package com.finalproject_sns.domain;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted_at is null") // 조회 시 사용
@SQLDelete(sql = "UPDATE post SET deleted_at=current_timestamp WHERE post_id = ?") // 삭제가 발생하면 쿼리 실행
public class Post extends BaseEntity {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;
    private String body;
    private String title;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 부모 엔티티 삭제 자식 엔티티 삭제
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt = null;

    public void update(String title, String body) {
        this.title = title;
        this.body = body;
    }
}
