package pl.codeve.inspectorbudget.security;

import lombok.Data;

@Data
class LoginRequest {
    private String usernameOrEmail;
    private String password;
}
