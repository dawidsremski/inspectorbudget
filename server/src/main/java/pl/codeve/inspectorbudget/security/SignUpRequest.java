package pl.codeve.inspectorbudget.security;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
class SignUpRequest {
    @NotBlank
    @Size(min = 5, max = 40)
    private String name;
    @NotBlank
    @Size(min = 5, max = 15)
    private String userName;
    @NotBlank
    @Email
    @Size(max = 40)
    private String email;
    @NotBlank
    @Size(min = 6, max = 20)
    private String password;
    @NotBlank
    @Size(min = 6, max = 20)
    private String repeatedPassword;
    private Long avatarId;
    @NotBlank
    private String reCAPTCHAResponse;
}