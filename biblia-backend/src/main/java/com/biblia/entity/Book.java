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
@Table(name = "book", schema = "biblia")
public class Book {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "book_id")
    private long bookId;
    @Basic
    @Column(name = "ISBN")
    private String ISBN;
    @Basic
    @Column(name = "title", nullable = false)
    private String title;
    @Basic
    @Column(name = "alias")
    private String alias;
    @Basic
    @Column(name = "image_url")
    private String imageUrl;
    @Basic
    @Column(name = "series_id")
    private Integer seriesId;
    @Basic
    @Column(name = "series")
    private String series;
    @Basic
    @Column(name = "publisher_id")
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
    @Column(name = "published_year")
    private Integer publishedYear;
    @Basic
    @Column(name = "language")
    private String language;
    @Basic
    @Column(name = "pages_no")
    private Integer pagesNo;
    @Basic
    @Column(name = "description")
    private String description;
    @Basic
    @Column(name = "rating")
    private Float rating;
    @Basic
    @Column(name = "fahasa_link")
    private String fahasaLink;
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

    @ManyToMany
    @JoinTable(name = "book_genre",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @ToString.Exclude
    private List<Genre> genres;
}
