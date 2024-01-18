package com.biblia.model.author;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthorRequest {
    private String name;
    private String alias;
    private String photo;
    private String born;
    private String died;
    private String website;
    private String description;
}
