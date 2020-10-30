package com.example.projects.blogengine.model;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Entity
public class CaptchaCodes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private ZonedDateTime time;

    @Column(nullable = false, columnDefinition = "TINYTEXT")
    private String code;

    @Column(nullable = false, columnDefinition = "TINYTEXT")
    private String secretCode;

    @PrePersist
    private void prePersist(){
        time = ZonedDateTime.now();
    }
}
