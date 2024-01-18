package com.biblia.model.book;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookResponse {
    private Long bookId;
    private String ISBN;
    private String title;
    private String imageUrl;
    private Integer publisherId;
    private String publisher;
    private Integer issuingHouseId;
    private String issuingHouse;
    private Integer publishedYear;
    private String language;
    private Float rating;
    private List<AuthorResponse> authors;
}
