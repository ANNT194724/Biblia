package com.biblia.model.series;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SeriesSearchRequest {
    private int size;
    private int page;
    private String keyword;
    private Integer authorId;
    private Integer issuingHouseId;
}
