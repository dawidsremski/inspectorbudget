package pl.codeve.inspectorbudget.security;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
class SignUpRequest {
    @NotBlank
    @Size(max = 50)
    private String name;
    @NotBlank
    @Size(max = 20)
    private String userName;
    @NotBlank
    @Email
    @Size(max = 50)
    private String email;
    @NotBlank
    @Size(max = 20)
    private String password;
    private Long avatarId;
    @NotBlank
    private String reCAPTCHAResponse;
}