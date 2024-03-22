package com.biblia.model.series;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SeriesResponse {
    private Integer seriesId;
    private String title;
    private Integer issuingHouseId;
    private Integer vols;
    private Float rating;
    private String description;
    private Integer status;
    private List<AuthorResponse> authors;
    private List<String> covers;
}
