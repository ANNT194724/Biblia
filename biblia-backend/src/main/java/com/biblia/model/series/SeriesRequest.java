package com.biblia.model.series;

import com.biblia.model.book.BookAuthorRequest;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SeriesRequest {
    @NotBlank
    private String title;
    private String alias;
    private Integer issuingHouseId;
    private String issuingHouse;
    private String description;
    private Integer status;
    private List<BookAuthorRequest> seriesAuthors;
    private List<Long> bookIds;
}
