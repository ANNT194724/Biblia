package com.biblia.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Integer seriesId;
    @Basic
    @Column(name = "title", nullable = false)
    private String title;
    @Basic
    @Column(name = "alias")
    private String alias;
    @Basic
    @Column(name = "issuing_house_id")
    private Integer issuingHouseId;
    @Basic
    @Column(name = "issuing_house")
    private String issuingHouse;
    @Basic
    @Column(name = "description")
    private String description;
    @Basic
    @Column(name = "status", nullable = false)
    private Integer status;
    @Basic
    @Column(name = "created_time")
    private Timestamp createdTime;
    @Basic
    @Column(name = "updated_time")
    private Timestamp updatedTime;
    @Basic
    @JsonIgnore
    @Column(name = "updated_user")
    private Integer updatedUser;
    @Basic
    @JsonIgnore
    @Column(name = "delete_flag", nullable = false)
    private Integer deleteFlag;

    @ManyToMany
    @JoinTable(name = "series_author",
            joinColumns = @JoinColumn(name = "series_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id"))
    @ToString.Exclude
    private List<Author> authors;
}
