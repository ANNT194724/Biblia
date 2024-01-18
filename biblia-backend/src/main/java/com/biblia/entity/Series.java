package com.biblia.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Table(name = "series", schema = "biblia")
public class Series {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "series_id")
    private Long seriesId;
    @Basic
    @Column(name = "title", nullable = false)
    private String title;
    @Basic
    @Column(name = "alias")
    private String alias;
    @Basic
    @Column(name = "publisher_id", nullable = false)
    private Integer publisherId;
    @Basic
    @Column(name = "publisher")
    private String publisher;
    @Basic
    @Column(name = "issuing_house_id")
    private Integer issuingHouseId;
    @Basic
    @Column(name = "issuing_house")
    private String issuingHouse;
    @Basic
    @Column(name = "language")
    private String language;
    @Basic
    @Column(name = "description")
    private String description;
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

    @ManyToMany
    @JoinTable(name = "series_genre",
            joinColumns = @JoinColumn(name = "series_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @ToString.Exclude
    private List<Genre> genres;
}
