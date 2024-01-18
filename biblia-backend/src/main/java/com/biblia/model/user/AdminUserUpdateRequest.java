package com.biblia.model.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdminUserUpdateRequest {
    Integer userId;
    String roleCode;
    Integer status;
    Integer deleteFlag;
}
