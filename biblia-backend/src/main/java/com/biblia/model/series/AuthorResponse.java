package com.biblia.model.series;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthorResponse {
    private Integer seriesId;
    private Integer authorId;
    private String name;
    private String role;
}
