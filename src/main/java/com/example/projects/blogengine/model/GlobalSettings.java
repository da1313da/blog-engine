package com.example.projects.blogengine.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class GlobalSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String value;
}
