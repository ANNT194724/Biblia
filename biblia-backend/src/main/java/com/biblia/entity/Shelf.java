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
@Table(name = "shelf", schema = "biblia")
public class Shelf {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "shelf_id")
    private Long shelfId;
    @Basic
    @Column(name = "book_id")
    private Long bookId;
    @Basic
    @Column(name = "user_id")
    private String userId;
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
    @Column(name = "delete_flag", nullable = false)
    private Integer deleteFlag;
}
