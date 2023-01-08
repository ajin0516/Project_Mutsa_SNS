package com.finalproject_sns.domain;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.springframework.security.core.parameters.P;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "alarm")
@SQLDelete(sql = "UPDATE alarm SET deleted_at=now() where alarm_id = ?")
public class Alarm extends BaseEntity{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "alarm_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    private Long fromUserId;
    private Long targetId;
    private String text;


    public Alarm (User user, Post post, String message, AlarmType alarmType) {
        this.fromUserId = user.getId();
        this.targetId = post.getId();
        this.text = message;
        this.alarmType = alarmType;
        this.user = user;
    }
}
