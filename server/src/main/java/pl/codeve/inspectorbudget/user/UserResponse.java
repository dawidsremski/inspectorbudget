package pl.codeve.inspectorbudget.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
class UserResponse {
    @NonNull
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String userName;
    @NonNull
    private String email;
    private Long avatarId;
}
