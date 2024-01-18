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
@Table(name = "book_author", schema = "biblia")
public class BookAuthor {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "book_author_id")
    private long bookAuthorId;
    @Basic
    @Column(name = "book_id", nullable = false)
    private Long bookId;
    @Basic
    @Column(name = "author_id", nullable = false)
    private Integer authorId;
    @Basic
    @Column(name = "role")
    private String role;
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
    private int deleteFlag;
}
