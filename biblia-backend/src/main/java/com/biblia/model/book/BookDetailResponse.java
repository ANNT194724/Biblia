package com.biblia.model.book;

import com.biblia.entity.Book;
import com.biblia.entity.Review;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookDetailResponse {
    private Book book;
    private List<AuthorResponse> authors;
    private Review userReview;
}
