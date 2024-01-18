package com.biblia.model.publisher;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PublisherCreateRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private String website;
    private String facebook;
    private String address;
    private String logoUrl;
    private String description;
}
