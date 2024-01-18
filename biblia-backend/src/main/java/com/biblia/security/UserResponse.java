package com.biblia.security;

import com.biblia.entity.RefreshToken;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResponse {
    private String token;
    private RefreshToken refreshToken;
    private final String type = "Bearer";
    private UserPrincipal user;
    private List<String> roles;
}
