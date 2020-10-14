package com.example.projects.blogengine.model;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Entity
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private Byte isActive;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModerationType moderationStatus;
    //FK-simple
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id", nullable = false)
    private Users moderatorId;
    //FK-simple
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users userId;
    @Column(nullable = false)
    private ZonedDateTime time;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;
    @Column(nullable = false)
    private Integer viewCount;
}
