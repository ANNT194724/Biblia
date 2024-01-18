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
@Table(name = "series_genre", schema = "biblia")
public class SeriesGenre {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "series_genre_id")
    private String seriesGenreId;
    @Basic
    @Column(name = "series_id", nullable = false)
    private Long seriesId;
    @Basic
    @Column(name = "genre_id", nullable = false)
    private Integer genreId;
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
