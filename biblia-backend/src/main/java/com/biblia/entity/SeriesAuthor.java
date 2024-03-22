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
@Table(name = "series_author", schema = "biblia")
public class SeriesAuthor {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "series_author_id")
    private long seriesAuthorId;
    @Basic
    @Column(name = "series_id", nullable = false)
    private Integer seriesId;
    @Basic
    @Column(name = "author_id", nullable = false)
    private Integer authorId;
    @Basic
    @Column(name = "role")
    private String role;
    @Basic
    @Column(name = "created_time")
    private Timestamp createdTime;
}
