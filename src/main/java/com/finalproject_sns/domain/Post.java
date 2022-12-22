package com.finalproject_sns.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;
    private String body;
    private String title;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
