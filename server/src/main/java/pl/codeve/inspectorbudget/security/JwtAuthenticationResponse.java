package pl.codeve.inspectorbudget.security;

import lombok.Data;

@Data
class JwtAuthenticationResponse {
    private String accessToken;
    private final String tokenType = "Bearer";

    JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}