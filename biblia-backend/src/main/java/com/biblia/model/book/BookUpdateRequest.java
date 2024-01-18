package com.biblia.model.book;

import com.biblia.entity.Author;
import com.biblia.entity.Genre;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookUpdateRequest {
    private String ISBN;
    @NotBlank
    private String title;
    private String imageUrl;
    private String alias;
    private List<Author> authors;
    private List<Genre> genres;
    private Integer publisherId;
    private String publisher;
    private Integer issuingHouseId;
    private String issuingHouse;
    private Integer publishedYear;
    private String language;
    private Integer pagesNo;
    private String description;
    private Integer quantity;
    private String fahasaLink;
    private Integer status;
}