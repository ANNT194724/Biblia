package com.biblia.model.book;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookSearchRequest {
    Integer page;
    Integer size;
    String keyword;
    Integer authorId;
    Integer publisherId;
    Integer issuingHouseId;
    Integer status;
    String sortBy;
    Integer sortDirection;
}
