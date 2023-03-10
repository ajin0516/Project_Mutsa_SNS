package com.finalproject_sns.domain;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;


import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted_at is null") // 조회 시 사용
@SQLDelete(sql = "UPDATE comment SET deleted_at=current_timestamp WHERE comment_id =?")
public class Comment extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    private String comment;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt = null;

    public void update(String comment) {
        this.comment = comment;
    }
}
