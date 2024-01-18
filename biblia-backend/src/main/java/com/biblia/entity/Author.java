package com.biblia.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Table(name = "author", schema = "biblia")
public class Author {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "author_id")
    private Integer authorId;
    @Basic
    @Column(name = "name", nullable = false)
    private String name;
    @Basic
    @Column(name = "alias")
    private String alias;
    @Basic
    @Column(name = "photo")
    private String photo;
    @Basic
    @Column(name = "born")
    private String born;
    @Basic
    @Column(name = "died")
    private String died;
    @Basic
    @Column(name = "website")
    private String website;
    @Basic
    @Column(name = "description")
    private String description;
    @Basic
    @Column(name = "status")
    private Integer status;
    @Basic
    @Column(name = "created_time")
    private Timestamp createdTime;
    @Basic
    @Column(name = "updated_time")
    private Timestamp updatedTime;
    @Basic
    @Column(name = "updated_user")
    private Integer updatedUser;
    @Basic
    @Column(name = "delete_flag")
    private Integer deleteFlag;
}
