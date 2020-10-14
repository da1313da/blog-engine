package com.example.projects.blogengine.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Tags {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
}
