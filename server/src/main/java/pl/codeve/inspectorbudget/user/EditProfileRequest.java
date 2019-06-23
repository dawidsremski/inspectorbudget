package pl.codeve.inspectorbudget.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
class EditProfileRequest {
    @Size(min = 5, max = 40)
    private String name;
    @Email
    @Size(max = 40)
    private String email;
//    @Size(min = 6, max = 20)
    private String password;
//    @Size(min = 6, max = 20)
    private String repeatedPassword;
    private Long avatarId;
}
